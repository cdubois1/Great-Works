#ifndef VIGENERESQUARE_H
#define VIGENERESQUARE_H
#include <iostream>
#include <fstream>
#include <vector>
#include <map>
#include <sstream>
#include <string>
#include <algorithm>
using namespace std;

class VIGENERESQUARE{
	public:
	char vigSq[26][26];
	void loadSquare();
	char keyLetter, plaintextLetter, ciphertextLetter;
	char encryptLetter(char keyLetter, char plaintextLetter);
	char decryptLetter(char keyLetter, char ciphertextLetter);
	
	
	
};
#endif