#include "VIGENERESQUARE.h"
#include <iostream>
#include <fstream>
#include <vector>
#include <map>
#include <sstream>
#include <string>
#include <algorithm>
using namespace std;
void encryptPlainTextFile();
void decryptCiphertextFile();
//================================================================================
int main()
{
	while(1){
		cout << "SELECT WHAT YOU WOULD LIKE TO DO:" << endl;
		cout << "1. Encrypt a plaintext file. " << endl;
		cout << "2. Decrypt a ciphertext file. " << endl;
		cout << "3. End this program. " << endl;
		int n;
		cin >> n;
		
		switch(n){
			case 1:
				encryptPlainTextFile();
				break;
			case 2:
				decryptCiphertextFile();
				break;
			case 3:
				exit(0);
				break;
			default: 
				cout << "invalid selection, GOODBYE!";
				exit(0);
				break;
		}
	}
} //END MAIN
//================================================================================
void encryptPlainTextFile(){
	string fileName;
	ifstream ifs;
	ofstream ofs;
	string keyword;
	VIGENERESQUARE vg;
	vg.loadSquare(); //end setup
	//ENCRYPTION
	cout << "Enter the Keyword for encryption: ";
	cin >> keyword;
	cout << endl << "Enter the name of the file containing the plaintext: ";
	cin >> fileName;
	
	//FILE MANIPULATION
	cout << "trying to open: " << fileName;
	ifs.open(fileName); //get file and open it
	if(!ifs.good()) //check file is good
	{
		cout << "failed to open input file" << endl;
	}
	else
	{
		ofs.open("Ciphertext.txt"); //open the output file
		if(!ofs.good())
			cout << "failed to do the thing" << endl;
		else{
			cout << endl << "opened files... beginning encryption" << endl;
			string inputString;
			string outputString;
			while(ifs.good()){
				ifs >> inputString; //get the string of plaintext
				outputString = inputString; 
				for(int i=0; i<inputString.length(); i++) //for each character of plain text
					outputString[i] = vg.encryptLetter(keyword[(i%keyword.size()+1)], inputString[i]); //encrypt the text
				ofs << outputString; //output the line of ciphertext to file
			}
			ifs.close();
			ofs.close();
			cout << "The text has been encrypted" << endl;
		}
	}
	cin.get();
}
//================================================================================
void decryptCiphertextFile(){
	string fileName;
	ifstream ifs;
	ofstream ofs;
	string keyword;
	VIGENERESQUARE vg;
	vg.loadSquare(); //end setup
	//DECRYPTION
	cout << "Enter the Keyword for decryption: ";
	cin >> keyword;
	cout << endl << "Enter the name of the file containing the ciphertext: ";
	cin >> fileName;
	
	//FILE MANIPULATION
	cout << "trying to open: " << fileName;
	ifs.open(fileName); //get file and open it
	if(!ifs.good()) //check file is good
	{
		cout << "failed to open input file" << endl;
	}
	else
	{
		ofs.open("Plaintext.txt"); //open the output file
		if(!ofs.good())
			cout << "failed to do the thing" << endl;
		else{
			cout << endl << "opened files... beginning decryption" << endl;
			string inputString;
			string outputString;
			while(ifs.good()){
				ifs >> inputString; //get the string of ciphertext
				outputString = inputString; 
				for(int i=0; i<inputString.length(); i++) //for each character of text
					outputString[i] = vg.decryptLetter(keyword[(i%keyword.size()+1)], inputString[i]); //encrypt the text
				ofs << outputString; //output the line of ciphertext to file
			}
			ifs.close();
			ofs.close();
			cout << "The text has been encrypted" << endl;
		}
	}
	cin.get();
}
//================================================================================
//================================================================================




