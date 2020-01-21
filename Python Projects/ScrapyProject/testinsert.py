import psycopg2


hostname = 'localhost'
username = 'power_user'
password = '$poweruser'
database = 'shirts'

def queryShirts(conn) :
	cur = conn.cursor()
	#cur.execute("""INSERT INTO shirts_attributes(asin, name, price, rank) VALUES (%s,%s,%s,%s)""", ('B02', 'test 2', '1', 10))
	cur.execute("""SELECT * FROM shirts_attributes""")
	rows = cur.fetchall()
	for row in rows:
	    print(row[1])

conn = psycopg2.connect(host = hostname, user = username, password = password, dbname = database)
queryShirts(conn)
conn.close()
