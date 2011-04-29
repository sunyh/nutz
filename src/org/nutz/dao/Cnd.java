package org.nutz.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;

/**
 * 是 Condition 的一个实现，这个类给你比较方便的方法来构建 Condition 接口的实例。
 * 
 * <h4>在 Dao 接口中使用</h4><br>
 * 
 * 比如一个通常的查询:
 * <p>
 * List<Pet> pets = dao.query(Pet.class,
 * Cnd.where("name","LIKE","B%").asc("name"), null);
 * 
 * <h4>链式赋值示例</h4><br>
 * Cnd.where("id", ">", 34).and("name","LIKE","T%").asc("name"); <br>
 * 相当于<br>
 * WHERE id>34 AND name LIKE 'T%' ORDER BY name ASC
 * <p>
 * Cnd.orderBy().desc("id"); <br>
 * 相当于<br>
 * ORDER BY id DESC
 * 
 * <h4>使用代码模板示例</h4><br>
 * <code>
 * Map<String, Object> map = new HashMap<String, Object>();<br>
 * map.put("name", "比尔盖茨");<br>
 * map.put("age", "50");<br>
 * Cnd.wrap("name='${name}' AND age>${age}", map);<br>
 * </code>
 * 相当于<br>
 * WHERE name='比尔盖茨' AND age>50
 * <p>
 * Cnd.wrap 第二个参数传入 map 或 pojo 类型均可正常转换
 * 
 * <h4 style=color:red>你还需要知道的是:</h4><br>
 * <ul>
 * <li>你设置的字段名，是 java 的字段名 -- 如果 Entity 里有，那么会被转换成数据库字段名
 * <li>如果你设置的是 entity 中不存在的 java 字段名，则被认为是数据库字段名，将直接使用
 * <li>你的值，如果是字符串，或者其他类字符串对象（某种 CharSequence），那么在转换成 SQL 时，会正确被单引号包裹
 * <li>你的值如果是不可理解的自定义对象，会被转化成字符串处理
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.Condition
 */
public class Cnd implements OrderBy, ExpGroup {

	/*------------------------------------------------------------------*/
	public static Condition format(String format, Object... args) {
		return new SimpleCondition(format, args);
	}

	public static Condition wrap(String str) {
		return new SimpleCondition((Object) str);
	}
	
	public static Condition wrap(String sql, Object value) {
		return new SimpleCondition( new CharSegment(sql).setBy(value));
	}

	public static Expression exp(String name, String op, Object value) {
		return new Exp(name, op, value);
	}

	public static ExpGroup exps(String name, String op, Object value) {
		return new Cnd(Cnd.exp(name, op, value));
	}

	public static ExpGroup exps(Expression e) {
		return new Cnd(e);
	}

	public static Cnd where(String name, String op, Object value) {
		return new Cnd(Cnd.exp(name, op, value));
	}

	public static Cnd where(Expression e) {
		return new Cnd(e);
	}

	public static OrderBy orderBy() {
		return new Cnd();
	}

	/*------------------------------------------------------------------*/

	Cnd() {
		bys = new LinkedList<By>();
	}

	protected Cnd(Expression e) {
		exps = new LinkedList<Expression>();
		exps.add(e);
		bys = new LinkedList<By>();
	}

	private List<By> bys;

	public OrderBy asc(String name) {
		bys.add(new By(name, "ASC"));
		return this;
	}

	public OrderBy desc(String name) {
		bys.add(new By(name, "DESC"));
		return this;
	}

	List<Expression> exps;

	private Cnd add(String s, Expression e) {
		exps.add(new Lop(s));
		exps.add(e);
		return this;
	}

	public Cnd and(Expression e) {
		return add(" AND ", e);
	}

	public Cnd and(String name, String op, Object value) {
		return and(Cnd.exp(name, op, value));
	}

	public Cnd or(Expression e) {
		return add(" OR ", e);
	}

	public Cnd or(String name, String op, Object value) {
		return or(Cnd.exp(name, op, value));
	}

	public Cnd andNot(Expression e) {
		return add(" AND NOT ", e);
	}

	public Cnd andNot(String name, String op, Object value) {
		return andNot(Cnd.exp(name, op, value));
	}

	public Cnd orNot(Expression e) {
		return add(" OR NOT ", e);
	}

	public Cnd orNot(String name, String op, Object value) {
		return orNot(Cnd.exp(name, op, value));
	}

	public String toSql(Entity<?> entity) {
		StringBuilder sb = new StringBuilder();
		if (exps != null) {
			for (Iterator<Expression> it = exps.iterator(); it.hasNext();) {
				it.next().render(sb, entity);
			}
		}
		if (bys.size() > 0) {
			if (sb.length() > 0)
				sb.append(' ');
			sb.append("ORDER BY");
			for (Iterator<By> it = bys.iterator(); it.hasNext();) {
				it.next().render(sb, entity);
				if (it.hasNext())
					sb.append(",");
			}
		}
		return sb.toString();
	}

	public void render(StringBuilder sb, Entity<?> en) {
		sb.append('(').append(toSql(en)).append(')');
	}

	/*------------------------------------------------------------------*/
	private static class By {

		By(String name, String by) {
			this.name = name;
			this.by = by;
		}

		private String name;
		private String by;

		public void render(StringBuilder sb, Entity<?> en) {
			String colName = name;
			if (null != en) {
				EntityField ef = en.getField(name);
				if (null != ef)
					colName = ef.getColumnName();
			}
			sb.append(' ').append(colName).append(' ').append(by);
		}
	}

	/*------------------------------------------------------------------*/
	private static class Lop implements Expression {
		private String s;

		private Lop(String txt) {
			this.s = txt;
		}

		public void render(StringBuilder sb, Entity<?> en) {
			sb.append(s);
		}
	}

	/*------------------------------------------------------------------*/
	private static class Exp implements Expression {

		private static final Pattern ptn = Pattern.compile("IS|LIKE|IN", Pattern.CASE_INSENSITIVE);

		Exp(String name, String op, Object value) {
			this.name = name;
			this.op = Strings.trim(op);
			if (ptn.matcher(this.op).find())
				this.op = this.op.toUpperCase();
			this.value = value;
		}

		private String name;
		private String op;
		private Object value;

		public void render(final StringBuilder sb, Entity<?> en) {
			if (null != en) {
				EntityField ef = en.getField(name);
				sb.append(null != ef ? ef.getColumnName() : name);
			} else
				sb.append(name);
			// IN
			if ("IN".equals(op) || "NOT IN".equals(op)) {
				sb.append(" ").append(op).append(" ");
				if (null == value)
					throw Lang.makeThrow("SQL '%s' null : %s", op, sb);
				// 如果是集合或者数组
				if (value instanceof Collection<?> || value.getClass().isArray()) {
					sb.append('(');
					Lang.each(value, new Each<Object>() {
						public void invoke(int i, Object ele, int length) throws ExitLoop,
								LoopException {
							sb.append(Sqls.formatSqlFieldValue(ele)).append(',');
						}
					});
					sb.setCharAt(sb.length() - 1, ')');
				}
				// 否则当作一般字符串处理
				else
					sb.append(Sqls.formatSqlFieldValue(value));
			}
			// IS (NOT) NULL
			// 根据 Jay(fjayblue@gmail.com) 的建议，此处应该考虑两种情况
			// 即，IS NULL 和 IS NOT NULL
			else if (null == value && op.startsWith("IS")) {
				sb.append(' ').append(op).append(" NULL");
			}
			// LIKE || IS
			else if ("LIKE".equals(op) || "IS".equals(op)) {
				sb.append(' ').append(op).append(' ').append(Sqls.formatSqlFieldValue(value));
			}
			// Others
			else {
				sb.append(op).append(Sqls.formatSqlFieldValue(value));
			}
		}
	}

}
