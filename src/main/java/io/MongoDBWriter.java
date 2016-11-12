package io;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import beans.Exportable;

public class MongoDBWriter implements IWriter {

	private MongoClient client;
	private MongoDatabase db;
	private MongoCollection<Document> collection;
	
	public MongoDBWriter() {
		this.client = new MongoClient();
		this.db = client.getDatabase("myDB");
		this.collection = db.getCollection("reviews");
	}
	
	@Override
	public void writeline(Exportable exportable) {
		// TODO Auto-generated method stub
	}

}
