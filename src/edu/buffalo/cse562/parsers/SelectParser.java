package edu.buffalo.cse562.parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.Union;
import edu.buffalo.cse562.operators.OperatorTest;
import edu.buffalo.cse562.utility.Utility;

public class SelectParser {

	@SuppressWarnings("unchecked")
	/**
	 * Parse a Select statement, and execute it.
	 * @param statement
	 */
	public static void parseStatement(Statement statement){
		SelectBody body = ((Select) statement).getSelectBody();
		
		if(body instanceof PlainSelect){
<<<<<<< HEAD
		/*	String from = returnFromItem(body);
			Expression condition= returnConditionItem(body);
			ArrayList<SelectExpressionItem> list = (ArrayList<SelectExpressionItem>)
					((PlainSelect) body).getSelectItems();
			String dataFileName = from.toLowerCase() + ".dat";
			dataFileName = Utility.dataDir.toString() + File.separator + dataFileName; */
			ArrayList<Object> parameters = getParameters(body);
			
			//OperatorTest.executeSelect(new File(dataFileName), from, condition,list);
			OperatorTest.executeSelect(new File((String)parameters.get(0)), 
					(String)parameters.get(1), 
					(Expression)parameters.get(2),
					(ArrayList<SelectExpressionItem>)parameters.get(3));
=======
			String from = ((PlainSelect) body).getFromItem().toString();
			ArrayList<Join> joins = (ArrayList<Join>) ((PlainSelect) body).getJoins();
			Expression condition= ((PlainSelect) body).getWhere();
			ArrayList<SelectExpressionItem> list = (ArrayList<SelectExpressionItem>)
					((PlainSelect) body).getSelectItems();
			String dataFileName = from.toLowerCase() + ".dat";
			dataFileName = Utility.dataDir.toString() + File.separator + dataFileName;
			OperatorTest.execute(new File(dataFileName), from, condition, list, joins);
>>>>>>> origin/master
		}
		else if(body instanceof Union){
			ArrayList<PlainSelect> plainSelects = new ArrayList(((Union) body).getPlainSelects());
			ArrayList<ArrayList<Object>> selectStatementsParameters = new ArrayList<ArrayList<Object>>(); 
			
			for(PlainSelect p : plainSelects){
				selectStatementsParameters.add(getParameters(p));
			}
			OperatorTest.executeUnion(selectStatementsParameters);
		}
	}
	
	public static String returnFromItem(SelectBody body){
		return ((PlainSelect) body).getFromItem().toString();
	}
	
	public static Expression returnConditionItem(SelectBody body){
		return ((PlainSelect) body).getWhere();
	}
	
	
	
	public static ArrayList<Object> getParameters(SelectBody body){
		//list of parameters in the sequence - From Item, Condition Item,  Select Items, DataFileName
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(Utility.dataDir.toString() + File.separator + ((PlainSelect) body).getFromItem().toString() + ".dat");
		parameters.add(((PlainSelect) body).getFromItem().toString());
		parameters.add(((PlainSelect) body).getWhere());
		parameters.add((ArrayList<SelectExpressionItem>)((PlainSelect) body).getSelectItems());		
		return parameters;
	}
	
	
}