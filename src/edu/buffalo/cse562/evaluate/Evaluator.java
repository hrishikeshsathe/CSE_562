package edu.buffalo.cse562.evaluate;

import java.sql.SQLException;
import java.util.HashMap;

import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.Eval;
import edu.buffalo.cse562.utility.Utility;

public class Evaluator extends Eval{

	private HashMap<String, Integer> schema;
	private Object[] tuple;

	public Evaluator(HashMap<String, Integer> table, Object[] tuple)
	{
		this.schema=table;
		this.tuple=tuple;
	}
	public LeafValue eval(Column c) throws SQLException {
		String t = "";
		int columnID = 0;
		if(c.getTable()!= null && c.getTable().getName() != null){
			t = c.getTable().getName();
			if(schema.containsKey(t+"."+c.getColumnName())){
				columnID = schema.get(t+"."+c.getColumnName());
				return (LeafValue) tuple[columnID];
			}else{
				for(String key: schema.keySet()){
					String x = key.substring(key.indexOf(".") + 1, key.length());
					if(x.equals(c.getTable()+"."+c.getColumnName())){
						columnID = schema.get(key);
					}
				}
				return (LeafValue) tuple[columnID];
			}
		}
		else{ 
			if(Utility.alias.containsKey(c.getColumnName()))
			{
				if(schema.containsKey(Utility.alias.get(c.getColumnName()).toString())){
					columnID = schema.get(Utility.alias.get(c.getColumnName()).toString());
					
				}else{
					for(String key: schema.keySet()){
						String x = key.substring(key.indexOf(".") + 1, key.length());
						if(x.equals(Utility.alias.get(c.getColumnName()).toString())){
							columnID = schema.get(key);
						}
					}
					
				}
				return (LeafValue) tuple[columnID];
				
			}
			else
			{
				for(String key: schema.keySet()){
					String x = key.substring(key.indexOf(".") + 1, key.length());
					if(x.equals(c.getColumnName())){
						columnID = schema.get(key);
					}
				}
			}
			return (LeafValue) tuple[columnID];
		}
	}

}
