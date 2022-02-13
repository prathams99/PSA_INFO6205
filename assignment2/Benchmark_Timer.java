/*
 * Copyright (c) 2018. Phasmid Software
 */

package edu.neu.coe.info6205.util;

import edu.neu.coe.info6205.sort.elementary.InsertionSort;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static edu.neu.coe.info6205.util.Utilities.formatWhole;

/**
 * This class implements a simple Benchmark utility for measuring the running time of algorithms.
 * It is part of the repository for the INFO6205 class, taught by Prof. Robin Hillyard
 * <p>
 * It requires Java 8 as it uses function types, in particular, UnaryOperator&lt;T&gt; (a function of T => T),
 * Consumer&lt;T&gt; (essentially a function of T => Void) and Supplier&lt;T&gt; (essentially a function of Void => T).
 * <p>
 * In general, the benchmark class handles three phases of a "run:"
 * <ol>
 *     <li>The pre-function which prepares the input to the study function (field fPre) (may be null);</li>
 *     <li>The study function itself (field fRun) -- assumed to be a mutating function since it does not return a result;</li>
 *     <li>The post-function which cleans up and/or checks the results of the study function (field fPost) (may be null).</li>
 * </ol>
 * <p>
 * Note that the clock does not run during invocations of the pre-function and the post-function (if any).
 *
 * @param <T> The generic type T is that of the input to the function f which you will pass in to the constructor.
 */
public class Benchmark_Timer<T> implements Benchmark<T> {

    /**
     * Calculate the appropriate number of warmup runs.
     *
     * @param m the number of runs.
     * @return at least 2 and at most m/10.
     */
    static int getWarmupRuns(int m) {
        return Integer.max(2, Integer.min(10, m / 10));
    }

    /**
     * Run function f m times and return the average time in milliseconds.
     *
     * @param supplier a Supplier of a T
     * @param m        the number of times the function f will be called.
     * @return the average number of milliseconds taken for each run of function f.
     */
    @Override
    public double runFromSupplier(Supplier<T> supplier, int m) {
        logger.info("Begin run: " + description + " with " + formatWhole(m) + " runs");
        // Warmup phase
        final Function<T, T> function = t -> {
            fRun.accept(t);
            return t;
        };
        new Timer().repeat(getWarmupRuns(m), supplier, function, fPre, null);

        // Timed phase
        return new Timer().repeat(m, supplier, function, fPre, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun, Consumer<T> fPost) {
        this.description = description;
        this.fPre = fPre;
        this.fRun = fRun;
        this.fPost = fPost;
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun) {
        this(description, fPre, fRun, null);
    }

    /**
     * Constructor for a Benchmark_Timer with only fRun and fPost Consumer parameters.
     *
     * @param description the description of the benchmark.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, Consumer<T> fRun, Consumer<T> fPost) {
        this(description, null, fRun, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer where only the (timed) run function is specified.
     *
     * @param description the description of the benchmark.
     * @param f           a Consumer function (i.e. a function of T => Void).
     *                    Function f is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, Consumer<T> f) {
        this(description, null, f, null);
    }

    private final String description;
    private final UnaryOperator<T> fPre;
    private final Consumer<T> fRun;
    private final Consumer<T> fPost;

    final static LazyLogger logger = new LazyLogger(Benchmark_Timer.class);

    public static void main(String[] args) {
        Random rand = new Random();
        InsertionSort insertion_sort = new InsertionSort();

        for (int n = 100; n < 5000; n = n * 2) {

            /*
             * Random Array
             * Add random integers to the arraylist
             */
            ArrayList<Integer> random_list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                random_list.add(rand.nextInt(n));
            }
            // toArray
            Integer[] randArray = random_list.toArray(new Integer[0]);
            // Run benchmark
            Benchmark<Boolean> benchmarkRandom = new Benchmark_Timer<>(
                    "randomSort", b -> {
                insertion_sort.sort(randArray.clone(), 0, randArray.length);
            });
            double resultRandom = benchmarkRandom.run(true, 10);

            /*
             * Ordered Array
             * Add ordered integers to the arraylist
             */
            ArrayList<Integer> ordered_list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                ordered_list.add(i + 1);
            }
            // toArray
            Integer[] srtdArray = ordered_list.toArray(new Integer[0]);
            // Run benchmark
            Benchmark<Boolean> benchmarkArranged = new Benchmark_Timer<>(
                    "arrangedSort", b -> {
                insertion_sort.sort(srtdArray.clone(), 0, srtdArray.length);
            });
            double resultOrganised = benchmarkArranged.run(true, 10);

            /*
             * Reversed Array
             * Add reversed integers to the arraylist
             */
            ArrayList<Integer> reverse_list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                reverse_list.add(n - i);
            }
            // toArray
            Integer[] revArray = reverse_list.toArray(new Integer[0]);
            // Run benchmark
            Benchmark<Boolean> benchmarkReversed = new Benchmark_Timer<>(
                    "reverseSort", b -> {
                insertion_sort.sort(revArray.clone(), 0, revArray.length);
            });
            double resultReversed = benchmarkReversed.run(true, 10);

            /*
             * Partial Array
             * Add partial integers to the arraylist
             */
            ArrayList<Integer> partial_list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (i > n / 2) {
                    partial_list.add(rand.nextInt(n));
                } else {
                    partial_list.add(i);
                }
            }
            // toArray
            Integer[] parArray = partial_list.toArray(new Integer[0]);
            // Run benchmark
            Benchmark<Boolean> benchmarkPartial = new Benchmark_Timer<>(
                    "partialSort", b -> {
                insertion_sort.sort(parArray.clone(), 0, parArray.length);
            });
            double resultPartial = benchmarkPartial.run(true, 10);

            System.out.println("N : " + n);
            System.out.println("Random: " + resultRandom);
            System.out.println("Ordered: " + resultOrganised);
            System.out.println("Reversed: " + resultReversed);
            System.out.println("Partial: " + resultPartial);
        }

    }
}
