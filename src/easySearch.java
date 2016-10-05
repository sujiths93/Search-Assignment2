import java.io.File;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.ConstantScoreWeight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.DFISimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class easySearch {
	
	public static void main(String args[]){
		try{
			String queryString="enter the query string here";
			Query query;
			String indexPath="C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\index";
			Analyzer analyzer=new StandardAnalyzer();
			QueryParser parser=new QueryParser("TEXT",analyzer);
			query=parser.parse(queryString);
			System.out.println(query);
			IndexReader reader=DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
			//Default Similarity has been deprecated
			ClassicSimilarity dSimi=new ClassicSimilarity();
			//Get the segments of the index
			List<LeafReaderContext> leafContexts = reader.getContext().reader().leaves();
			
		}
		 catch (ParseException e) {
				System.out.println("ERROR WHILE PARSING");
			}
		
}
