package edu.buffalo.cse562.operators;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import edu.buffalo.cse562.utility.Utility;

public class OperatorTest {

	static boolean isAggregate = false;
	static boolean isGroupBy = false;
	static boolean isJoin;

	public static void executeSelect(File file, String tableName, Expression condition,ArrayList<SelectExpressionItem> list, ArrayList<Join> joins, boolean allColumns){
		isJoin = false;
		ArrayList<String> joinTables = new ArrayList<String>();
		Operator oper = new ReadOperator(file, tableName);
		ArrayList<Function> functions=null;
		Expression onExpression = null;

		if(joins != null){
			if(joins.get(0).toString().contains("JOIN")){
				isJoin = true;
				joinTables.add(joins.get(0).getRightItem().toString());
				onExpression = joins.get(0).getOnExpression();
			}else{
				for(int i = 0; i<joins.size(); i++){
					joinTables.add(joins.get(i).toString());
				}
			}
			oper = new CrossProductOperator(oper, joinTables, tableName);
			tableName = oper.getTableName();
		}
		if(onExpression != null){
			oper = new SelectionOperator(oper, Utility.tables.get(tableName), onExpression);
		}
		if(condition != null)
			oper= new SelectionOperator(oper, Utility.tables.get(tableName), condition);
		if(!allColumns)
		{
			functions= new ArrayList<Function>();

			for(int i=0; i<list.size(); i++){
				if(list.get(i).getExpression() instanceof Function){
					functions.add((Function) list.get(i).getExpression());
					isAggregate = true;
				}
				else{
					if(isAggregate){
						isGroupBy = true;
					}
				}
			}
		}
		if(isAggregate && !isGroupBy)
			oper=new AggregateOperator(oper, Utility.tables.get(tableName), functions);
		else if(isGroupBy)
			System.out.println("Group");
		else
			oper = new ProjectOperator(oper, list, tableName,allColumns);
		dump(oper);
	}

	@SuppressWarnings("unchecked")
	public static void executeUnion(ArrayList<ArrayList<Object>> selectStatementsParameters){
		ArrayList<Operator> oper = new ArrayList<Operator>();

		for(ArrayList<Object> statementParameters : selectStatementsParameters){
			Operator o = new ReadOperator(new File((String)statementParameters.get(0)), (String)statementParameters.get(1));
			if((Expression)statementParameters.get(2) != null){
				o = new SelectionOperator(o, Utility.tables.get((String)statementParameters.get(1)), (Expression)statementParameters.get(2));
			}

			o = new ProjectOperator(o, (ArrayList<SelectExpressionItem>)statementParameters.get(3), (String)statementParameters.get(1), false);
			oper.add(o);
		}
		dumpUnion(oper);
	}

	public static void dumpUnion(ArrayList<Operator> oper){
		ArrayList<String> unionTuples = new ArrayList<String>();
		for(Operator op : oper){
			Object[] row = op.readOneTuple();
			while(row != null){
				String tuple = "";
				for(Object col: row){
					tuple += (col.toString()+" | ");
				}
					unionTuples.add(tuple);
					System.out.println(tuple);
				row = op.readOneTuple();
			}
		}
		System.out.println(unionTuples.size());
	}

	public static void dump(Operator input){
		if(input instanceof AggregateOperator){
			Object[] row = input.readOneTuple();
			int i = 0;
			for(i=0; i<row.length-1; i++){
				System.out.print(row[i].toString()+"|");
			}
			System.out.print(row[i].toString());
		}
		else{
			Object[] row = input.readOneTuple();
			while(row != null){
				int i = 0;
				for(i=0; i<row.length-1; i++){
					if(row[i] instanceof StringValue)
						System.out.print(((StringValue)row[i]).getNotExcapedValue() + "|");
					else
						System.out.print(row[i]+"|");
				}
				System.out.print(row[i].toString());
				System.out.println();
				row = input.readOneTuple();
			}
		}
	}
}
