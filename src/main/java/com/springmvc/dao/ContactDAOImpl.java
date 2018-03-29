package com.springmvc.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.springmvc.model.Contact;

public class ContactDAOImpl implements ContactDAO {
	
	private JdbcTemplate jdbcTemplate;
	 
    public ContactDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

	public void saveOrUpdate(Contact contact) {
		if (contact.getId() > 0) {
	        // update
	        String sql = "UPDATE contact SET contactname=?, email=?, address=?, "
	                    + "telephone=? WHERE contact_id=?";
	        jdbcTemplate.update(sql, contact.getName(), contact.getEmail(),
	                contact.getAddress(), contact.getTelephone(), contact.getId());
	    } else {
	       /* // insert
	        String sql = "INSERT INTO contact (contactname, email, address, telephone)"
	                    + " VALUES (?, ?, ?, ?)";
	        jdbcTemplate.update(sql, contact.getName(), contact.getEmail(),
	                contact.getAddress(), contact.getTelephone());*/
	    	 Connection connection = null;
	    	 final String procedureCall = "{call insert_contact(?,?,?,?)}";
	    	 try {
				connection = jdbcTemplate.getDataSource().getConnection();
				CallableStatement callableSt = connection.prepareCall(procedureCall);
				callableSt.setString(1, contact.getName());
				callableSt.setString(2, contact.getEmail());
				callableSt.setString(3, contact.getAddress());
				callableSt.setString(4, contact.getTelephone());
				//Call Stored Procedure
				callableSt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
	 

	}

	public void delete(int contactId) {
		 String sql = "DELETE FROM contact WHERE contact_id=?";
		    jdbcTemplate.update(sql, contactId);

	}

	public Contact get(int contactId) {
		 String sql = "SELECT * FROM contact WHERE contact_id=" + contactId;
		    return jdbcTemplate.query(sql, new ResultSetExtractor<Contact>() {
		 
		        public Contact extractData(ResultSet rs) throws SQLException,
		                DataAccessException {
		            if (rs.next()) {
		                Contact contact = new Contact();
		                contact.setId(rs.getInt("contact_id"));
		                contact.setName(rs.getString("contactname"));
		                contact.setEmail(rs.getString("email"));
		                contact.setAddress(rs.getString("address"));
		                contact.setTelephone(rs.getString("telephone"));
		                return contact;
		            }
		 
		            return null;
		        }
		 
		    });
	}

	public List<Contact> list() {
		String sql = "SELECT * FROM contact";
	    List<Contact> listContact = jdbcTemplate.query(sql, new RowMapper<Contact>() {
	 
	        public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
	            Contact aContact = new Contact();
	 
	            aContact.setId(rs.getInt("contact_id"));
	            aContact.setName(rs.getString("contactname"));
	            aContact.setEmail(rs.getString("email"));
	            aContact.setAddress(rs.getString("address"));
	            aContact.setTelephone(rs.getString("telephone"));
	 
	            return aContact;
	        }
	 
	    });
	 
	    return listContact;
	}

}
