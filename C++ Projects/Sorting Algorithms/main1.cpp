#include <stdio.h>
#include <algorithm>
#include <iostream>
#include <ctime> 
#include <vector>
#include <utility> 
#include <fstream>
#include <iterator>
#include <math.h>
//=========================================================================================
//=========================================================================================
/*
 *This program uses Quicksort, Mergesort, and Insertion sort to perform
 *two hybrid sorts: Quick Insertion Sort and Merge Quicksort
 *this is to demonstrate the differences in efficiency and analysis of algorithms
*/
using namespace std;

void quickSort(int arr[], int left, int right) {

      int i = left, j = right;
	  int tmp;
      int pivot = min(arr[left], arr[right]);
      /* partition */
      while (i <= j) {
            while (arr[i] < pivot)
                  i++;
            while (arr[j] > pivot)
                  j--;
            if (i <= j) {
                  tmp = arr[i];
                  arr[i] = arr[j];
                  arr[j] = tmp;
                  i++;
                  j--;
            }
      };
      /* recursion */
      if (left < j)
            quickSort(arr, left, j);
      if (i < right)
            quickSort(arr, i, right);
}//taken from http://www.algolist.net/Algorithms/Sorting/Quicksort
//=========================================================================================
//MERGESORT
// Merges two subarrays of arr[]. 
// First subarray is arr[l..m] 
// Second subarray is arr[m+1..r] 
void merge(int arr[], int l, int m, int r) 
{ 
    int i, j, k; 
    int n1 = m - l + 1; 
    int n2 =  r - m; 
  
    /* create temp arrays */
    int L[n1], R[n2]; 
  
    /* Copy data to temp arrays L[] and R[] */
    for (i = 0; i < n1; i++) 
        L[i] = arr[l + i]; 
    for (j = 0; j < n2; j++) 
        R[j] = arr[m + 1+ j]; 
  
    /* Merge the temp arrays back into arr[l..r]*/
    i = 0; // Initial index of first subarray 
    j = 0; // Initial index of second subarray 
    k = l; // Initial index of merged subarray 
    while (i < n1 && j < n2) 
    { 
        if (L[i] <= R[j]) 
        { 
            arr[k] = L[i]; 
            i++; 
        } 
        else
        { 
            arr[k] = R[j]; 
            j++; 
        } 
        k++; 
    } 
  
    /* Copy the remaining elements of L[], if there 
       are any */
    while (i < n1) 
    { 
        arr[k] = L[i]; 
        i++; 
        k++; 
    } 
  
    /* Copy the remaining elements of R[], if there 
       are any */
    while (j < n2) 
    { 
        arr[k] = R[j]; 
        j++; 
        k++; 
    } 
} 
  
/* l is for left index and r is right index of the 
   sub-array of arr to be sorted */
//=========================================================================================
void mergeSort(int arr[], int l, int r) 
{ 
    if (l < r) 
    { 
        // Same as (l+r)/2, but avoids overflow for 
        // large l and h 
        int m = l+(r-l)/2; 
  
        // Sort first and second halves 
        mergeSort(arr, l, m); 
        mergeSort(arr, m+1, r); 
  
        merge(arr, l, m, r); 
    } 
} //Taken from: https://www.geeksforgeeks.org/merge-sort/
//=========================================================================================
//SelectoinSort
/* Function to sort an array using insertion sort*/
void insertionSort(int arr[], int n) 
{ 
   int i, key, j; 
   for (i = 1; i < n; i++) 
   { 
       key = arr[i]; 
       j = i-1; 
  
       /* Move elements of arr[0..i-1], that are 
          greater than key, to one position ahead 
          of their current position */
       while (j >= 0 && arr[j] > key) 
       { 
           arr[j+1] = arr[j]; 
           j = j-1; 
       } 
       arr[j+1] = key; 
   } 
} //Taken from: https://www.geeksforgeeks.org/insertion-sort/
//=========================================================================================
//Quick insertionSort
void quickInsertion(int arr[], int left, int right) {

      int i = left, j = right;
	  int tmp;
      int pivot = min(arr[left], arr[right]);
	  if(left-right <10)
		  insertionSort(arr, left-right);
      /* partition */
	  else{
		  while (i <= j) {
				while (arr[i] < pivot)
					  i++;
				while (arr[j] > pivot)
					  j--;
				if (i <= j) {
					  tmp = arr[i];
					  arr[i] = arr[j];
					  arr[j] = tmp;
					  i++;
					  j--;
				}
		  };
		  /* recursion */
		  if (left < j)
				quickSort(arr, left, j);
		  if (i < right)
				quickSort(arr, i, right);
	  }
}
//=========================================================================================
//Quick mergeSort
void multiMerge(int arr[], int l, int m, int r) 
{ 
    int i, j, k; 
	int* p[m];
	vector<int*> priority_queue;
	vector<int*>::iterator it;
	
	//create a temporary 2d array
	int temparray[m][m];
	for(i=0; i<m; i++)
		for(j=0; j<m; j++)
			temparray[i][j]= arr[i*m+j];
	
	//create an array of pointers to the first node of each subarray
   for(j=0; j<m; j++){
		p[i]=&temparray+(j*m); //set the pointers to the array address of the first element of each subarray
   }
	//create a sorted priority queue of pointers.
	//it= priority_queue.begin();
	int* t;
	for(i=0; i<m; i++){
		int temp=99999999;
		for(k=0; k<m; k++)
			if(*p[k]<temp){
				t=p[k];
				temp= *p[k];
			}
		priority_queue.push_back(t);
	}
    /* Merge the temp arrays back into arr[l..r]*/
    i = 0; // Initial index of first subarray 
    j = 0; // Initial index of second subarray 
    k = 0; // Initial index of merged subarray 
	while(i<(r-l)){
		it= priority_queue.begin();
		int* t= priority_queue.front(); //take the first element in the queue (lowest value)
		priority_queue.erase(priority_queue.begin()); //remove it from the queue
		arr[i]= *t; //set the value at i to be the value of the current smallest element of the subarrays
		t++; //increment the pointer to the sub-array
		if(((&temparray[0][0]-t)/m)<=m) //if the position of the pointer has not exceeded that of its subarray
			for(int j=0; j<m; j++){
				int* temp=priority_queue.at(j);
				if(*t<*temp) //if t is less than a value already in the queue, put it in front of that value
					priority_queue.insert(it, t);
				it++;
				if(j+1==m) //if t is greater than all values already queued, place it at the back of the queue
					priority_queue.push_back(t);
			}
		i++;
	}
}
//=========================================================================================
void merge(int arr[], int l, int m, int r) 
{ 
    int i, j, k; 
    int n1 = l +m + 1; 
    int n2 =  r - m; 
  
    /* create temp arrays */
    int L[n1], R[n2]; 
  
    /* Copy data to temp arrays L[] and R[] */
    for (i = 0; i < n1; i++) 
        L[i] = arr[l + i]; 
    for (j = 0; j < n2; j++) 
        R[j] = arr[(l+m) + 1+ j]; 
  
    /* Merge the temp arrays back into arr[l..r]*/
    i = 0; // Initial index of first subarray 
    j = 0; // Initial index of second subarray 
    k = l; // Initial index of merged subarray 
    while (i < n1 && j < n2) 
    { 
        if (L[i] <= R[j]) 
        { 
            arr[k] = L[i]; 
            i++; 
        } 
        else
        { 
            arr[k] = R[j]; 
            j++; 
        } 
        k++; 
    } 
  
    /* Copy the remaining elements of L[], if there 
       are any */
    while (i < n1) 
    { 
        arr[k] = L[i]; 
        i++; 
        k++; 
    } 
  
    /* Copy the remaining elements of R[], if there 
       are any */
    while (j < n2) 
    { 
        arr[k] = R[j]; 
        j++; 
        k++; 
    } 
}
//=========================================================================================
void mergeQuick(int arr[], int l, int r) 
{ 
    if (l < r) 
    { 
        int m = sqrt(l+(r-l)); 
  
        // Sort the subarrays
        for(int i=0; i<m; i++){
			if(i==0)
				quickSort(arr, i*m, (i+1)*m); //I think this needs to be i*m+1 for all sub-arrays except the first, need to account for fist and last sub-array
			else if (i==m-1)
				quickSort(arr, i*m, r);
			else
				quickSort(arr, i*m+1, (i+1)*m);
		}
		cout << "Quickly sorted merge arrays!" << endl;
		//now i have sqrt(n) sorted sub-arrays, but how do i merge them together?
        //multiMerge(arr, l, m, r); 
		for(int j=0; j<(m/2); j++)
			merge(arr, j*m, m, 2*(j+1)*m);
		merge(arr, r-2*m, m, r); //ensure the last array is sorted
		//now I have an array that has been pairwise sorted	
    } 
}
//=========================================================================================
void fillArray(int arr[], int size){

	srand(time(NULL));
	for(int i=0; i<size; i++)
		arr[i]= rand();
}
//=========================================================================================
//=========================================================================================
int main(int argc, char* argv[])
{
	ofstream ofs;
	int n = argc;
	//int arr[n];
	//fillArray(arr, n);
	ofs.open("OutputTime.txt");
	if(ofs.good()){
		
		//Quicksort
		int arr[]={5,6,8,1,2,6,3,8,0};
		int start_s=clock();
		quickSort(arr, 0, 8);
		int stop_s=clock();
		cout << "total runtime: " << (float) (stop_s-start_s)/(CLOCKS_PER_SEC) << endl << "final array: ";
		ofs << "quicksort";
		ofs << (stop_s-start_s)/double(CLOCKS_PER_SEC)*1000;
		cout << endl;
		
		//Mergesort
		int arr1[]={1,5,3,6,2,7,0,9,10,8,11,14,12,17,13};
		start_s=clock();
		mergeSort(arr1, 0, 14);
		stop_s=clock();
		cout << "total runtime: " << (stop_s-start_s)/double(CLOCKS_PER_SEC)*1000 << endl << "final array: ";
		ofs << "mergesort";
		ofs << (stop_s-start_s)/double(CLOCKS_PER_SEC)*1000;
		cout << endl;
		
		//Quick Insertion Sort
		start_s=clock();
		quickInsertion(arr, 0, n); 
		stop_s=clock();
		cout << "total runtime: " << (stop_s-start_s)/double(CLOCKS_PER_SEC)*1000 << endl;
		ofs << "quickInsertion";
		ofs << (stop_s-start_s)/double(CLOCKS_PER_SEC)*1000;
		
		//Merge Quicksort
		start_s=clock();
		mergeQuick(arr, 0, n); 
		stop_s=clock();
		cout << "total runtime: " << (stop_s-start_s)/double(CLOCKS_PER_SEC)*1000 << endl;
		ofs << "mergeQuick";
		ofs << (stop_s-start_s)/double(CLOCKS_PER_SEC)*1000;
	}
	return 0;
}
//=========================================================================================
//=========================================================================================