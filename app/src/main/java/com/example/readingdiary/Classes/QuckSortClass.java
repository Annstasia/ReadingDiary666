package com.example.readingdiary.Classes;

import java.util.ArrayList;

public class QuckSortClass
{
//    public static void quickSort(ArrayList<>, int from, int to)
//    {// from и to - два условных места в массиве.
//        // from всегда должен быть равен 0, а to длине массива минус один. алгоритм потом их сам сдвигает
//        // arr это массив, который нужно сортировать
//
//
//        if (from < to)
//        {
//
//            int divideIndex = partition(arr, from, to);
//
//            quickSort(arr, from, divideIndex - 1);
//
//            quickSort(arr, divideIndex, to);
//
//        }
//    }
//
//
//
//    private static int partition(int[] arr, int from, int to)
//    {
//        int rightIndex = to;
//        int leftIndex = from;
//
//        int pivot = arr[from + (to - from) / 2];
//        while (leftIndex <= rightIndex)
//        {
//
//            while (arr[leftIndex] < pivot)
//            {
//                leftIndex++;
//            }
//
//            while (arr[rightIndex] > pivot)
//            {
//                rightIndex--;
//            }
//
//            if (leftIndex <= rightIndex)
//            {
//                swap(arr, rightIndex, leftIndex);
//                leftIndex++;
//                rightIndex--;
//            }
//        }
//        return leftIndex;
//    }
//
//    private static void swap(int[] array, int index1, int index2)
//    {
//        int tmp  = array[index1];
//        array[index1] = array[index2];
//        array[index2] = tmp;
//    }
//
//
//    public static void StrSort(String [] words, String x,List<String> coincide )
//    // worbs массив, котрый нужно сортировать
//    // в x передаём слово, которое нужно искать
//    //  coincide лист, в который переаются совпадения
//    {
//        for (int i = 0; i < words.length; i++) // проверка неупорядоченного массива на предмет совпадений и переброс совпадений в пустой массив
//        {                                       // из массива worbs в массив coincide
//
//            if (words[i].contains(x)) {
//
//                coincide.add(words[i]);
//
//            }
//        }
//
//        for (int z = 0; z < coincide.size(); z++) // вывод массива coincide
//        {
//
//            System.out.println(coincide.get(z));
//
//        }
//    }
//
//    public static void sortTitles(Note note){
//
//
//    }
//
//}
//
//static class SortTitles{
//    private ArrayList<Note> notes;
//    private int start;
//    private int end;
//    private int order;
//    private String comp;
//
//    public SortTitles(ArrayList<Note> notes, int start, int end, int order, int comp){
//        this.notes = notes;
//        this.start = start;
//        this.end = end;
//        this.order = order;
//        this.comp = comp;
//        quickSort(start, end);
//    }
//
//    public void quickSort(int from, int to) {
//        if (from < to) {
//            int divideIndex = partition(from, to);
//            quickSort(from, divideIndex - 1);
//            quickSort(divideIndex, to);
//        }
//    }
//    private int partition(int from, int to)
//    {
//        int rightIndex = to;
//        int leftIndex = from;
//
//        pivot = getComparable((RealNote) notes.get(from + (to - from) / 2));
////        String leftNote;
////        String rightNote;
//
//
//        while (leftIndex <= rightIndex)
//        {
//
//            while (getComparable((RealNote) notes.get(leftIndex)) < pivot)
//            {
//                leftIndex++;
//            }
//
//            while (getComparable((RealNote) notes.get(rightIndex)) > pivot)
//            {
//                rightIndex--;
//            }
//
//            if (leftIndex <= rightIndex)
//            {
//                swap(arr, rightIndex, leftIndex);
//                leftIndex++;
//                rightIndex--;
//            }
//        }
//        return leftIndex;
//    }
//
//
//    public String getComparable(RealNote realNote){
//        if (comp.equals("title")){
//            return realNote.getTitle();
//        }
//        if (comp.equals("author")){
//            return realNote.getAuthor();
//        }
//    }
//
//    private void swap(int index1, int index2)
//    {
//        int tmp  = notes.get(index1);
//        notes.set(index1, notes.index2);
//        notes.set(index2, tmp);
//    }

}