package com.example.readingdiary.Classes;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class QuickSort
{
    public static void main(String[] args) // по идеи его тут не должно быть, это для тестов. потом будем просто вызывать нужный метод из класса
    {
        // cортировка чисел от меньшего к большему
        System.out.println("Выполняется выстрая сортировка:");
        int testLen = 10000; // количпество чисел, которым заполять массив
        int [] array = new int[testLen];
        for (int i = 0; i < testLen; i++) // заполнение array случайными числами
        {
            array[i] = (int)Math.round(Math.random() * 10000);
        }

       /* for (int i = 0; i < testLen; i++) // заполнение array упорядоченными числами
        {
            array[i] =i;
        }*/

        // поиск по алфовиту
        Scanner in = new Scanner(System.in);
        PrintStream out = System.out;
        String [] words = {"Good", "morning", "run", "jump", "box", "book", "big jump", "jump"}; //массив со вссем данными
        String x = "jump";// переменная в которую мы передаём искомую строку
        List<String> coincide = new ArrayList<>();


        // вызов методов
        quickSort(array, 0, array.length - 1); //вызов метода сортировки чисел
//        measureTime(()->quickSort(array, 0, testLen - 1));// вызов метода таймера (можно убрать,я его делал для тестов)
        StrSort (words, x,coincide); // вызов метода сортировки по алфовиту
    }

    public static void quickSort(int[] arr, int from, int to)
    {// from и to - два условных места в массиве.
        // from всегда должен быть равен 0, а to длине массива минус один. алгоритм потом их сам сдвигает
        // arr это массив, который нужно сортировать


        if (from < to)
        {

            int divideIndex = partition(arr, from, to);

            quickSort(arr, from, divideIndex - 1);

            quickSort(arr, divideIndex, to);

        }
    }

    private static int partition(int[] arr, int from, int to)
    {
        int rightIndex = to;
        int leftIndex = from;

        int pivot = arr[from + (to - from) / 2];
        while (leftIndex <= rightIndex)
        {

            while (arr[leftIndex] < pivot)
            {
                leftIndex++;
            }

            while (arr[rightIndex] > pivot)
            {
                rightIndex--;
            }

            if (leftIndex <= rightIndex)
            {
                swap(arr, rightIndex, leftIndex);
                leftIndex++;
                rightIndex--;
            }
        }
        return leftIndex;
    }

    private static void swap(int[] array, int index1, int index2)
    {
        int tmp  = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
    }
    private static void measureTime(Runnable task)
    {
        long startTime = System.currentTimeMillis();
        task.run();
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Затраченное время: " + elapsed + " ms");
    }

    public static void StrSort(String [] words, String x,List<String> coincide )
    // worbs массив, котрый нужно сортировать
    // в x передаём слово, которое нужно искать
    //  coincide лист, в который переаются совпадения
    {
        for (int i = 0; i < words.length; i++) // проверка неупорядоченного массива на предмет совпадений и переброс совпадений в пустой массив
        {                                       // из массива worbs в массив coincide

            if (words[i].contains(x)) {

                coincide.add(words[i]);

            }
        }

        for (int z = 0; z < coincide.size(); z++) // вывод массива coincide
        {

            System.out.println(coincide.get(z));

        }
    }

}