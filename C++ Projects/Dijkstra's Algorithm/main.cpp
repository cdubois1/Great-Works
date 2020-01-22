#include <stdio.h>
#include <cmath>
//=========================================================================================================================================//
//=========================================================================================================================================//
/*
 * This program runs Dijkstra's algorithm
 */
struct Vertex{
	int x;
	int y;
	int distance;
	int penultimate;
	bool visited;
	
	//bool operator<(const Vertex& o) const{ //should allow the struct to be used as a priority queue
	//	return distance < o.distance; //will this return the LEAST distance?
	//}
};
//=========================================================================================================================================//
//=========================================================================================================================================//
int* setupWeightMatrix(string filename, int numVertices, int numEdges, Vertex v[]){
	string str;
	
		for(int i=0; i<numVertices; i++){ //get the positions of the vertices
			getline(ifs, str); //get line of text
			char *p= strok(str, " "); //tokenize it
			p=strtok(NULL, " "); //throw out the first value, we dont need it, p now equals the x position of vertex i
			int x= atoi(p); //should I use atoi or stoi?
			p=strtok(NULL, " "); //get y value
			int y= atoi(p);
			v[i].x=x; //store the x and y values for vertex i
			v[i].y=y;
		}
		//file pionter should now be on the first line of the edges
		
		for(int i=0; i<numEdges; i++){
			getline(ifs, str);
			char *p= strtok(str, " ");
			int first_vertex= atoi(p);
			p=strtok(NULL, " ");
			int second_vertex= atoi(p);
			weightMatrix[first_vertex][second_vertex]= sqrt( pow((v[first_vertex].x - v[second_vertex].x),2) + 
											pow((v[first_vertex].y - v[second_vertex].y),2) ); 
											//weightmatrix[first][second] = distance between first vertex and second
		}
		ifs.close();
		return weightMatrix;
}
//=========================================================================================================================================//
int findShortestDistance(Vertex v[]){
	int vertex;
			int temp = 9999;
	for(int i=0 i<sizeof(v); i++)
		if((v[i].visited==false) && (v[i].distance < temp)){
			temp=v[i].distance;
			vertex=i;
		}
	return vertex;
}
//=========================================================================================================================================//
void dijkstra(int weightMatrix[][], int start_vertex, Vertex v[]){
	int size= sizeof(v);
	v[start_vertex].distance= 0;
	v[start_vertex].penultimate= -1; //setup starting vertex
	v[start_vertex].visited=true;
	
	for(int i=0; i<size; i++){
		if(i != start_vertex){
			v[i].distance = weightMatrix[i][start_vertex]; //the distance from i to the start vertex
			v[i].visited=false;
		}
		if((v[i].distance!=999999) && (i != start_vertex)) //if the distance is not infinity IE there is a path
			v[i].penultimate= start_vertex;
		else
			v[i].penultimate= -1; //if no path from i to start vertex set penultimate to -1
	} //setup vertices is complete
	
	for(int j=0; j<size-1; j++){
		int next_vertex = findShortestDistance(v);
		v[next_vertex].visited=true;
		for(int k=0; k<size; k++)
			if((v[k].visited==false) && (weightMatrix[next_vertex][k]!=999999) && ((v[next_vertex].distance + weightMatrix[next_vertex][k])< v[k].distance)){
				v[k].penultimate=next_vertex;
				v[k].distance = v[next_vertex].distance + weightMatrix[next_vertex][k];
			}
				
	}
}
//=========================================================================================================================================//
//=========================================================================================================================================//
int main(int argc, char *argv[])
{
	string filename= argv[1];
	int start_vertex= atoi(argv[2]);
	int end_vertex= atoi(argv[3]);
	if(start_vertex > end_vertex){
		int temp = start_vertex;
		start_vertex= end_vertex;
		end_vertex=temp;
	}
		
	ifstream ifs;
	
	int numVertices, numEdges;
	
	ifs.open(filename); //open the file
	if(ifs.good()){
		getline(ifs, str); //get the first line of text
		char *p= strtok(str, " "); //tokenize the line to parse out the spaces
		numVertices= stoi(p, nullptr, 1); //the first value is the number of vertices
										//will this work is P is a char* and not string? stoi or atoi
		p=strtok(NULL, " "); //get second value
		numEdges= stoi(p, nullptr, 1); //second value is the number of edges
		
		int weightMatrix[numVertices][numVertices]; //make an integer matrix for use as the weight matrix
		for(int i=0; i<numVertices; i++)
			for(int k=0; k<numVertices; k++){
				if(i==k)
					weightMatrix[i][k]=0; //0's on the diagnal
				else
					weightMatrix[i][k]= 999999; //initalize the matrix to inifity's
			}
		
		Vertex v[numVertices]; //create an array of vertices
		int *weightMatrix= setupWeightMatrix(filename, numVertices, numEdges, v);
		
		dijkstra(weightMatrix, start_vertex, v);
		
		//print the path
		cout << "The shortest path from " << start_vertex << " to " << end_vertex << " is: " << endl;
		int node=end_vertex;
		while(v[node].penultimate!= -1){
			cout << v[node].penultimate << " --> ";
			node=v[node].penultimate;
		}
		cout << start_vertex << endl;
		cout << "Thanks for using Disjkstra's algorithm today! Goodbye." << endl
		int pause;
		cin >> pause;
	}
	else{
		cout << "Failed to open data file, closing...";
		cin.get();
		return 0;
	}
	
	return 0;
}
//=========================================================================================================================================//
//=========================================================================================================================================//