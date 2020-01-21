#include <iostream>
#include <cstdio>
#include <cstdlib>
#include <iomanip>
#include <fstream>
#include <new>
#include <climits>
#include <cstring>
#include <cctype>
using namespace std;
 
struct Node
{
    string key;
    Node* left;
    Node* right;
	Node* parent;
};
 
class SplayTree
{
    public:
	Node* root;
	
        SplayTree()
        {
			root = NULL;
        }
 
        Node* New_Node(string key)
        {
			cout<<"\n creating new node";
            Node* p_node = new Node;
            p_node->key = key;
            p_node->left = NULL;
			p_node->right = NULL;
			p_node->parent = NULL;
            return p_node;
        } 
 
    Node* Splay(Node* node) 
	{
	Node* newRoot;
	Node* p;
	Node* gp;
	bool done = false;
	while(!done)
	{
		cout<<"\n at beginning of splay loop.";
		if(node == NULL || root == NULL)
		{
			done = true; // done because node is the only node
		}
		else
		{
			p = node->parent; //get parent of node, save it in p
			gp = p->parent;  //get parent of p, save it in gp
			if(gp == NULL) //then p is the root (p has no parent)
			{
				if(node == p->left)//the node we are accessing is left
				{
					cout<<"\n splaying; node is to the left";
					node = L_Rotate(node,p);
					done = true;
				}
				else //the node is right
				{
					cout<<"\n splaying; node is to the right";
					node = R_Rotate(node,p);
					done = true;
				}
			}
			else //gp is not null (we will have to double rotate)
			{
				if(p == gp->left) //if p is gp's left child,
				{
					if(node == p->left) //and node is left of p (so left left)
					{
						//we need to do a LL Rotate (to get node to root)
						cout<<"\n splaying; node is to the left then left";
						node = LL_Rotate(node,p,gp);
					}
					else // node is left then right
					{
						//do an LR rotate
						cout<<"\n splaying; node is to the left then right";
						node = LR_Rotate(node,p,gp);
					}
				}
				else //p is to the right of gp 
				{
					if(node == p->left) //node is right then left
					{
						//do an RL rotate
						cout<<"\n splaying; node is to the right then left";
						node = RL_Rotate(node,p,gp);
					}
					else //the node is right then right
					{
						//do an RR rotate
						cout<<"\n splaying; node is to the right then right";
						node = RR_Rotate(node,p,gp);
					}
				}//end p is right of gp
			}//end gp is not null
		}//end of all the rotates
		if(!done)
		{
			if(!node->parent)
			{
				cout<<"\n splay complete.";
				done = true;
			}
		}
	}//end of loop
	cout<<"\n splay returning " << node->key;
	return node;
}//end of function


//==========================================

Node* L_Rotate(Node* node, Node* p)//node is left child of root(p is root)
{
	cout << "\n L rotating node " << node->key;
	p->left = node->right; 
	if(p->left != NULL)
	{
		p->left->parent = p;
	}
	node->right = p;
	node->parent = p->parent; // this would be null
	p->parent = node;
	node->parent = NULL; 
	return node;
}

Node* R_Rotate(Node* node,Node* p) //node is right child of root
{
	cout << "\n R rotating " << node->key;
	p->right = node->left;
	if(p->right != NULL)
	{
		p->right->parent = p;
	}
	
	node->left = p;
	node->parent = p->parent; // this would be null
	p->parent = node;
	node->parent = NULL;
	return node;
	
}

Node* LL_Rotate(Node* node,Node* p, Node* gp)
{
	cout<<"\n begin LL rotate. node = "<<node->key<<" p = "<<p->key<<" gp = " << gp->key;
	Node* ggp = gp->parent;
	gp->left = p->right;
	if(gp->left)
	{
		gp->left->parent = gp;
		cout << "\n setting gp->left->parent to gp";
	}
	p->right = gp;
	gp->parent = p;
	p->left = node->right;
	if(p->left)
	{
		p->left->parent = p;
		cout << "\n setting p->left->parent to p";
	}
	node->right = p;
	p->parent = node;
	node->parent = ggp;
	if(ggp != NULL)
	{
		if(gp == ggp->left)
		{
			ggp->left = node;
			node->parent = ggp;
        }
		
		else
		{
			ggp->right = node;
			node->parent = ggp;
        }
		
		cout << "\n Node "<< node->key <<"'s new parent is "<<ggp->key;
	}
	else
	    node->parent = NULL;
		return node;
	
}

Node* RR_Rotate(Node* node,Node* p, Node* gp)
{
	cout<<"\n begin RR rotate.";
	Node* ggp = gp->parent;
	gp->right = p->left;
	if(gp->right != NULL)
	{
		gp->right->parent = gp;
	}
	p->left = gp;
	gp->parent = p;
	p->right = node->left;
	if(p->right != NULL)
	{
		p->right->parent = p;
	}
	node->left = p;
	p->parent = node;
	node->parent = ggp;
	if(ggp != NULL)
	{
		if(gp == ggp->right)
			ggp->right = node;
		else
			ggp->left = node;
	}
	else
		return node;
}

Node* LR_Rotate(Node* node, Node* p, Node* gp) //node is left then right
{
	cout<<"\n begin LR rotate.";
	Node* ggp = gp->parent;
	gp->left = p->right; //p->right is node
	if(gp->left != NULL) //should never be null i think
	{
		gp->left->parent = gp; //set nodes parent to gp(might not have to do this)
	}
	gp->parent = ggp;
	p->right = node->left;
	if(p->right != NULL)
	{
		p->right->parent = p;
	}
	node->left = p;
	p->parent = node;
	//---------------------end of first rotation
	gp->left = node->right;
	if(gp->left != NULL)
	{
		gp->left->parent = gp;
	}
	node->right = gp;
	gp->parent = node;
	node->parent = ggp;
	if(ggp != NULL)
	{
		if(gp == ggp->left)
			ggp->right = node;
		else
			ggp->left = node;
	}
	else
		return node;
}

Node* RL_Rotate(Node* node, Node* p, Node* gp) //node is left then right
{
	cout<<"\n begin RL rotate.";
	Node* ggp = gp->parent;
	gp->right = p->left; //p->left is node
	if(gp->right != NULL) //should never be null i think
	{
		gp->right->parent = gp; //set nodes parent to gp(might not have to do this)
	}
	gp->parent = ggp;
	p->left = node->right;
	if(p->left != NULL)
	{
		p->left->parent = p;
	}
	node->right = p;
	p->parent = node;
	//---------------------end of first rotation
	gp->right = node->left;
	if(gp->right != NULL)
	{
		gp->right->parent = gp;
	}
	node->left = gp;
	gp->parent = node;
	node->parent = ggp;
	if(ggp != NULL)
	{
		if(gp == ggp->left)
			ggp->right = node;
		else
			ggp->left = node;
	}
	else
		return node;
}
///=============================================/////end rotatios


Node* Insert(string key, Node* node)
{
	if(node == NULL)
	{
		cout << "\n There are no nodes yet. inserting at root";
		root = New_Node(key);
		return root;
	}
	else if(key < node->key)
	{
		if(node->left == NULL)
		{
			cout << "\n inserting to the left of " << node->key;
			node->left = New_Node(key);
			node->left->parent = node;
			root = Splay(node->left);
			cout << "\n insert complete";
		}
		else
			Insert(key, node->left);
		
	}
	else
	{
		if(node->right == NULL)
		{
			cout << "\n inserting to the right of " << node->key;
			node->right = New_Node(key);
			node->right->parent = node;
			root = Splay(node->right);
			cout << "\n insert complete";
		}
		else
			Insert(key, node->right);
	}
	cout << "\n insert is returning the new root";
	return root;
}

Node* Access(string key, Node* node)
{
	if(node == NULL)
	{
		cout << "\n There are no nodes yet.";
		return NULL;
	}
	else if(node->key == key)
		return node;
	else if(key < node->key)
	{
		if(node->left->key == key)
		{
			cout << "\n found the node! ";
			root = Splay(node->left);
			cout << "\n node moved to root \n";
		}
		else
			Access(key, node->left);
		
	}
	else
	{
		if(node->right->key == key)
		{
			cout << "\n found the node! ";
			root = Splay(node->right);
			cout << "\n node moved to root \n";
		}
		else
			Access(key, node->right);
	}
	return root;
}


        Node* Delete(string key, Node* node)
        {
            Node* temp;
            if (!node)
                return NULL;

			cout << "\n delete: bringing node to root \n";
            node = Access(key, node); //bring key to delete to root
			InOrder(node);
			temp = node; //store the node in a temp pointer so we can delete it and still have it's pointers
            if (key != node->key)
                return node; //no clue
            else
            {
                if (!node->left) //if there is no left child
                {
					cout << "\n delete: there is no left child";
                    root = node->right; //set the right child as root
					cout << "\n delete: root is now " << root->key;
					root->parent = NULL;
					
                }
                else if(node->left) //if there is a left child
                {
					cout << "\n delete: there is a left child"; //so we will need to find a thing to place in root pos
					root = deleteMax(node->left);
					cout << "\n delete: root is now " << root->key;
					if(root != temp->left)
					{
						root->left = temp->left;
						root->left->parent = root;
					}
					if(temp->right)
					{
						root->right = temp->right; 
						root->right->parent = root;
					}
					root->parent = NULL;
                }
                free(temp);
                return root;
            }
        }
		
		Node* deleteMax(Node* node)
		{
			if(node->right) //if the left node has a right child
			{
				cout << "\n deleteMax: searching... ";
				node = deleteMax(node->right);
			}
			else
			{
				cout << "\n deleteMax: Found max. returning: " << node->key;
				return node;
			}
			if(node->parent != root)
			{
				node->parent->right = NULL;
			}
			return node;
		}
		
		void InOrder(Node* root, int indent=0)
		{
			
			if(root != NULL) 
			{
				if(root->right) 
					InOrder(root->right, indent+4);
				if (indent) 
				{
					std::cout << std::setw(indent) << ' ';
				}
				if(root->right) 
				{
					std::cout<<" /\n" << std::setw(indent) << ' ';
				}
				std::cout<<root->key << "\n ";
				if(root->left) 
				{
					std::cout << std::setw(indent) << ' ' <<" \\\n";
					InOrder(root->left, indent+4);
				}
			}
		}
		
		 
		void InOrder1(Node* root)
        {
            if (root)
            {
                InOrder1(root->left);
                cout<< "key: " <<root->key;
				if(root->parent)
                    cout<< " | parent: "<< root->parent->key;
                if(root->left)
                    cout<< " | left child: "<< root->left->key;
                if(root->right)
                    cout << " | right child: " << root->right->key;
                cout<< "\n";
                InOrder1(root->right);
            }
        }
		
		
		
};
 
int main()
{
    SplayTree *st;
    Node *root;
    root = NULL;
    string input;
	int choice;
	string buff;
	char filename[10];
	bool fail;
	ifstream inFile;
	int count;
	
	st = new SplayTree();
	cout << "filename?";
		cin.getline(filename,100,'\n');    
		while(cin.fail()) 
		{
			cout << "TRY AGAIN";
			cin.clear(); //this will clear the error flags. 
			cin.sync();// clears the buffer
			cin.getline(filename,100,'\n'); 
		}
		cin.clear();
		cin.sync();
		
		if(filename[0]=='\0')
		{
			strcpy(filename,"data.txt");
		}
		else if(!strstr(filename,"."))
		{
			strcat(filename, ".txt");
		}
		
		fail=0;
		inFile.open(filename);
		if(inFile.fail())			
		{
			cout << "Error opening file "<< filename << endl;
			system("pause");
			fail=true;
		}
		count=0;
		while(!inFile.eof()) 
		{
			inFile.ignore(999,'\n');
			count++;
		}
		inFile.clear();
		inFile.seekg(0, ios::beg);
		
		for(int i=1; i<count-1; i++) 
		{
			inFile >> buff;
			root = st->Insert(buff, root);
			cout<<"\nAfter Inserting "<<buff<<endl;
			st->InOrder(root);
		}
	
	
    while(1)
	{
        cout<<"\nSplay Tree Operations\n";
        cout<<"1. Insert "<<endl;
        cout<<"2. Delete"<<endl;
        cout<<"3. Access"<<endl;
        cout<<"4. Exit"<<endl;
        cout<<"Enter your choice: ";
        cin>>choice;
        switch(choice)
        {
        case 1:
            cout<<"Enter value to be inserted: ";
            cin>>input;
            root = st->Insert(input, root);
			cout<<"\nRoot is now "<<root->key<<endl;
            cout<<"\nAfter Inserting "<<input<<endl;
			st->root = root;
            st->InOrder(root);
			st->InOrder1(root);
            break;
        case 2:
            cout<<"Enter value to be deleted: ";
            cin>>input;
            root = st->Delete(input, root);
            cout<<"\nAfter Deleteing "<<input<<endl;
            st->InOrder(root);
			st->InOrder1(root);
            break;
        case 3:
            cout<<"Enter value to be accessed: ";
            cin>>input;
            root = st->Access(input, root);
            cout<<"\nAfter Accessing "<<input<<endl;
            st->InOrder(root);
			st->InOrder1(root);
            break;
 
        case 4:
            exit(1);
        default:
            cout<<"\nInvalid type! \n";
        }
    }
    cout<<"\n";
    return 0;
}
