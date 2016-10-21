//import java.io.FileWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
public class searchTRECtopics extends compareAlgorithms{	
    static ArrayList<storequery> o=new ArrayList<storequery>();;
	public searchTRECtopics() throws IOException {
		super();
		o=this.parsequery();
		addQueries(o);
	}
	void addQueries(ArrayList<storequery> o){
		for(int i=0;i<o.size();i++){
			o.get(i).desc=o.get(i).desc.trim();
			o.get(i).qid=o.get(i).qid.trim();
			o.get(i).title=o.get(i).title.trim();
			//System.out.println(o.get(i).qid+"\n"+o.get(i).title+"\n"+o.get(i).desc);
		}
	}
	public void generateScoresLongqueries(String fileName,ArrayList<storequery> queryobject) throws IOException, ParseException{
		FileWriter fwriter = new FileWriter(fileName);
		for(storequery sq:queryobject){
			//System.out.println("\n"+sq.qid+"\n");		
			easySearch es=new easySearch(sq.desc);
			List<Scores> finalScores=es.scoreTopDocs();
			writeScores(fwriter,finalScores,sq.qid);
		}
		fwriter.close();
		System.out.println("DONE");
	}
	public void generateScoresShortqueries(String fileName,ArrayList<storequery> queryObject) throws IOException, ParseException{
		FileWriter fwriter = new FileWriter(fileName);
		for(storequery sq:queryObject){
			//System.out.println("\n"+sq.qid+"\n");		
			easySearch es=new easySearch(sq.title);
			List<Scores> finalScores=es.scoreTopDocs();
			writeScores(fwriter,finalScores,sq.qid);
		}
		fwriter.close();
		System.out.println("DONE");
	}

	public static void writeScores(FileWriter fwriter,List<Scores> finalScores,String qid) throws IOException{
		int n=finalScores.size()>1000 ? 1000:finalScores.size();
		for(int i=0;i<n;i++){
			String temp="";
			temp+=qid+" 0 "+finalScores.get(i).docId+" "+i+" "+finalScores.get(i).score+" 1"+"\n";
			fwriter.append(temp);
		}
	}
	public static void main(String args[]) throws IOException, ParseException{
		searchTRECtopics s=new searchTRECtopics();
		s.generateScoresShortqueries("C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\TfIdfShortQuery.txt",o);
		s.generateScoresLongqueries("C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\TfIdfLongQuery.txt", o);
	}
}
