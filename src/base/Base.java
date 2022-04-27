/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Tsiory
 */
public class Base extends Connect {

    private final HashMap<String, String> where = new HashMap<>();
    private final HashMap<String, String> order_by = new HashMap<>();
    private final HashMap<String, String> join = new HashMap<>();

    public Base() {
    }

    private HashMap<String, String> getWhere() {
        return where;
    }

    private HashMap<String, String> getOrderBy() {
        return order_by;
    }

    public HashMap<String, String> getJoin() {
        return join;
    }

    private String setters(String methodName) {
        return ("set" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1));
    }

    private String getters(String methodName) {
        return ("get" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1));
    }

    public void where(String colonne, String value) {
        this.getWhere().put(colonne, value);
    }

    public void order_by(String colonne, String type) {
        this.getOrderBy().put(colonne, type);
    }

    public void join(String table, String id) {
        this.getJoin().put(table, id);
    }

    private String setWhereRequete() {
        String requete = " WHERE ";
        if (this.getWhere().isEmpty()) {
            return " ";
        }

        int i = 1;

        for (Map.Entry map : this.getWhere().entrySet()) {
            if (this.getWhere().size() == i) {
                requete += map.getKey() + "='" + map.getValue() + "'";
                break;
            } else {
                requete += map.getKey() + "='" + map.getValue() + "' AND ";
            }
            i++;
        }

        return requete;
    }

    private String setOrderByRequete() {
        String requete = "ORDER BY ";
        if (this.getOrderBy().isEmpty()) {
            return " ";
        }
        int i = 1;
        for (Map.Entry map : this.getOrderBy().entrySet()) {
            if (this.getOrderBy().size() == i) {
                requete += map.getKey() + " " + map.getValue();
                break;
            } else {
                requete += map.getKey() + " " + map.getValue() + " , ";
            }
            i++;
        }

        return requete;
    }

    private String setJoinRequete() {
        String requete = "JOIN ";
        if (this.getJoin().isEmpty()) {
            return " ";
        }
        int i = 1;
        for (Map.Entry map : this.getJoin().entrySet()) {
            if (this.getJoin().size() == i) {
                requete += map.getKey() + " ON " + map.getValue();
                break;
            }
            i++;
        }

        return requete;
    }

    public List<Base> get() throws Exception {
        try (Connection connexion = this.getConnect()) {
            String filter = "";
            String methodName = "";
            String className = this.getClass().getSimpleName();
            List<Base> data = new ArrayList<>();
            Object dbt = new Base();
            String req = "SELECT * FROM " + className + this.setWhereRequete() + "" + this.setOrderByRequete() + "" + this.setJoinRequete();
            PreparedStatement state = connexion.prepareStatement(req);
            ResultSet res = state.executeQuery();
            Field[] fields = this.getClass().getDeclaredFields();
            Object[] values = new Object[fields.length];
            while (res.next()) {
                Base tmp = this.getClass().newInstance();
                tmp.setAllSetters(res);
                data.add(tmp);
            }
            return data;
        }
    }

    private void setAllSetters(ResultSet rs) throws Exception {
        Class cls = this.getClass();
        for (Field field : cls.getDeclaredFields()) {
            for (Method method : cls.getDeclaredMethods()) {
                if ((method.getName().startsWith("set")) && (method.getName().length() == (field.getName().length() + 3))) {
                    if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
                        try {
                            method.setAccessible(true);
                            if (field.getType().getSimpleName().toLowerCase().endsWith("integer")) {
                                method.invoke(this, rs.getInt(field.getName().toLowerCase()));
                            } else if (field.getType().getSimpleName().toLowerCase().endsWith("long")) {
                                method.invoke(this, rs.getLong(field.getName().toLowerCase()));
                            } else if (field.getType().getSimpleName().toLowerCase().endsWith("string")) {
                                method.invoke(this, rs.getString(field.getName().toLowerCase()));
                            } else if (field.getType().getSimpleName().toLowerCase().endsWith("boolean")) {
                                method.invoke(this, rs.getBoolean(field.getName().toLowerCase()));
                            } else if (field.getType().getSimpleName().toLowerCase().endsWith("timestamp")) {
                                method.invoke(this, rs.getTimestamp(field.getName().toLowerCase()));
                            } else if (field.getType().getSimpleName().toLowerCase().endsWith("date")) {
                                method.invoke(this, rs.getDate(field.getName().toLowerCase()));
                            } else if (field.getType().getSimpleName().toLowerCase().endsWith("double")) {
                                method.invoke(this, rs.getDouble(field.getName().toLowerCase()));
                            } else if (field.getType().getSimpleName().toLowerCase().endsWith("float")) {
                                method.invoke(this, rs.getFloat(field.getName().toLowerCase()));
                            } else if (field.getType().getSimpleName().toLowerCase().endsWith("time")) {
                                method.invoke(this, rs.getTime(field.getName().toLowerCase()));
                            } else {
                                method.invoke(this, rs.getObject(field.getName().toLowerCase()));
                            }
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    public void insererTable() throws Exception {
        Connection conn = this.getConnect();
        String tableName = this.getClass().getSimpleName();
        conn.setAutoCommit(false);
        String query = "INSERT INTO " + tableName + "(";
        Field[] f = this.getClass().getDeclaredFields();
        query += f[0].getName();
        for (int i = 1; i < f.length; i++) {
            query += ("," + f[i].getName());
        }
        query += ") VALUES (";
        String seq = "select nextval(?)";
        String id = "";
        try {
            PreparedStatement sq = conn.prepareStatement(seq);
            sq.setString(1, tableName + "_SEQ");
            try (ResultSet rs = sq.executeQuery(seq)) {
                if (rs.next()) {
                    id = this.getClass().getSimpleName().toLowerCase() + rs.getString(1);
                }
            }
            query += "'" + id + "'";
            for (int i = 1; i < f.length; i++) {
                String field = f[i].getName();
                char[] nomField = field.toCharArray();
                nomField[0] = Character.toUpperCase(nomField[0]);
                String fi = new String(nomField);
                Method meth = this.getClass().getDeclaredMethod("get" + fi);
                query += ",'" + String.valueOf(meth.invoke(this));
                query += "'";

            }

            query += ")";
            System.out.println(query);
            Statement st = conn.createStatement();
            st.executeUpdate(query);
            st.close();

            conn.commit();

        } catch (Exception e) {
            /*String msg = e.getMessage();
			System.out.println(msg);*/
            e.printStackTrace();
            conn.rollback();
        }
    }

    public int insert() throws Exception {
        String methodName = "";
        String className = this.getClass().getSimpleName();
        String suf = "";
        String pref = "";
        String fin = "";
        Object temp;
        try (Connection connexion = this.getConnect()) {
            String req = "INSERT INTO " + className + " VALUES (nextval('" + className + "_seq'),";
            Field[] fields = this.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                suf = "";
                pref = ",";
                fin = "');";
                methodName = fields[i].getName();
                Method function = this.getClass().getMethod(getters(methodName), null);
                temp = function.invoke(this, null);
                if (temp != null) {
                    suf = ((temp != null) && (!temp.getClass().getSimpleName().equals("Date")) && (((("" + temp).split("\\.")).length) != 2) ? "'" : "");
                    pref = ((temp != null) && (!temp.getClass().getSimpleName().equals("Date")) && (((("" + temp).split("\\.")).length) != 2) ? "'," : ",");
                    if (temp != null) {
                        if (temp.getClass().getSimpleName().equals("Date")) {
                            fin = ");";
                        }
                        temp = ((temp.getClass().getSimpleName().equals("Date")) ? "timestamp '" + (((Date) temp).getYear() + 1900) + "-" + (((Date) temp).getMonth() + 1) + "-" + ((Date) temp).getDate() + " " + ((Date) temp).getHours() + ":" + ((Date) temp).getMinutes() + ":" + ((Date) temp).getSeconds() + "'" : temp);
                    }
                    if (i == fields.length - 1) {
                        if (temp == null) {
                            fin = ");";
                        }
                    }
                    req += suf + temp + ((i < fields.length - 1) ? pref : fin);
                }
            }
            System.out.println("Requete " + req);
            PreparedStatement state = connexion.prepareStatement(req);
            state.executeUpdate();
            connexion.commit();
            return 1;
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException | SQLException exception) {
            System.out.println(exception.getMessage() + " non inserer");
        }
        return 0;
    }

    public int delete() throws Exception {
        String className = this.getClass().getSimpleName();
        Object temp;
        Connection connexion = this.getConnect();
        try {
            String req = "DELETE FROM  " + className + " WHERE ";
            Field[] fields = this.getClass().getDeclaredFields();
            String methodName = fields[0].getName();
            Method function = this.getClass().getMethod(getters(methodName), null);
            temp = function.invoke(this, null);
            req += methodName + "='" + temp + "'";
            PreparedStatement statement = connexion.prepareStatement(req);
            statement.executeQuery();
            connexion.commit();
            return 1;
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException | SQLException exception) {
            System.out.println(exception.getMessage());
            connexion.commit();
            return 0;
        } finally {
            connexion.commit();
        }

    }

    public int update(Base oldData, Connection connection) throws Exception {
        String detection = "";
        String methodName = "";
        String className = this.getClass().getSimpleName();
        Object temp = new Object();
        try (Connection connexion = this.getConnect()) {
            String req = "UPDATE " + className + " SET ";
            Field[] fields = this.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                methodName = fields[i].getName();
                Method function = this.getClass().getMethod(getters(methodName), null);
                req += methodName + "='" + function.invoke(this, null) + ((i < fields.length - 1) ? "'," : "' WHERE ");
                if (fields[i].getName().equals("id")) {
                    detection += methodName + "='" + function.invoke(oldData, null) + "'";
                }
            }
            String requete = req + detection;
            PreparedStatement statement = connexion.prepareStatement(req);
            statement.executeQuery();
            connexion.commit();
            return 1;
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException | SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return 0;
    }
}
