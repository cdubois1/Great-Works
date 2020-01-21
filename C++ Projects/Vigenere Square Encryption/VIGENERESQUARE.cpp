#include "VIGENERESQUARE.h"
#include <iostream>
#include <fstream>
#include <vector>
#include <map>
#include <sstream>
#include <string>
#include <algorithm>
using namespace std;


	//Load the square from a file
	void VIGENERESQUARE:: loadSquare(){
		ifstream ifs;
		string input;
		ifs.open("VS.txt");
		if(!ifs.good()){
			cout << "Failed to load Vigenere Square";
		}
		else{
			for(int i=0; i<26; i++){
				ifs >> input; //get the line
				for(int j=0; j<26; j++)
					vigSq[i][j] = input[j]; //place the line
			}
		}
	}
		
	//Encrypt a plaintext letter using the given keyletter
	char VIGENERESQUARE:: encryptLetter(char keyLetter, char plaintextLetter){
		putchar(tolower(plaintextLetter)); //ensure letter is lowercase
		int position= plaintextLetter-96; //find its position in the alphabet
		for(int i=0; i<26; i++){
			if(vigSq[i][0]==keyLetter) //if this row in the Vigenere Square begins with the correct keyletter
				return vigSq[i][position]; //return the character in the correct position
		}
		return 0; //otherwise kill yourself
	}
	
	//Decrypt a ciphertext letter using the given keyletter
	char VIGENERESQUARE:: decryptLetter(char keyLetter, char ciphertextLetter){
		putchar(tolower(ciphertextLetter)); //ensure letter is lowercase
		for(int i=0; i<26; i++){
			if(vigSq[i][0]==keyLetter) //if this row begins with the correct keyletter
				for(int j=0;j<26; j++) //walk through the row until...
					if(vigSq[i][j]==ciphertextLetter) //the ciphertext character's position is found
						return vigSq[0][j]; //return the corresponding plaintext character for that position
		}
		return 0; //otherwise kill yourself
	}