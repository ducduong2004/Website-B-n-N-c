package dao;

import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

	private final Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User getUserRs(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password"));
		user.setEmail(rs.getString("email"));
		user.setAddress(rs.getString("address"));
		user.setIsAdmin(rs.getBoolean("is_admin"));
		user.setPhone(rs.getString("phone_number"));
		user.setImg(rs.getString("images"));
		return user;
	}

	public boolean editProfile(User user, String name, String email, int phone, String address) {
		String sql = "UPDATE users SET username = ?, email = ?, phone_number = ?, address = ? WHERE id = ?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, email);
			ps.setInt(3, phone);
			ps.setString(4, address);
			ps.setInt(5, user.getId()); // Sử dụng ID của người dùng để xác định bản ghi cần cập nhật

			return ps.executeUpdate() > 0; // Trả về số dòng bị ảnh hưởng
		} catch (SQLException e) {
			e.printStackTrace();
			return false; // Trả về false nếu có lỗi xảy ra
		}
	}

	public User editProfileUser(User user, String name, String email, int phone, String address) {
		String sql = "UPDATE users SET username = ?, email = ?, phone_number = ?, address = ? WHERE id = ?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, email);
			ps.setInt(3, phone);
			ps.setString(4, address);
			ps.setInt(5, user.getId()); // Sử dụng ID của người dùng để xác định bản ghi cần cập nhật

			if (ps.executeUpdate() > 0) {
				// Nếu cập nhật thành công, trả về đối tượng User đã được cập nhật
				user.setUsername(name);
				user.setEmail(email);
				user.setPhone(String.valueOf(phone)); // Chuyển đổi số điện thoại về dạng String
				user.setAddress(address);
				return user; // Trả về đối tượng User đã cập nhật
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveImg(String path, int id) {
		String sql = "UPDATE ListUser set img = ? where user_id = ? ";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, path);
			ps.setString(2, id + "");
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getUserImg(int id) throws SQLException {
		String sql = "SELECT img FROM ListUser WHERE user_id = ?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id + "");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public User getUser(int id) {
		String sql = "select * from ListUser where user_id=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id + "");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return getUserRs(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean updatePassword(String email, String password) throws SQLException {
		String query = "UPDATE users SET password=? WHERE email=?";
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, password);
		ps.setString(2, email);
		int row = ps.executeUpdate();
		System.out.println(row);
		return row > 0;
	}

	public boolean registerUser(User user) throws SQLException {
		String sql = "insert into users  (username, password, email, phone_number) " + "VALUES (?,?,?,?)";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getEmail());
			ps.setString(4, user.getPhone());
			ps.executeUpdate();
		} catch (Exception e) {
			throw new SQLException(e);
		}
		return true;
	}

	public boolean checkEmailExist(String email) {
		String sql = "select email from users where email=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString("email").equals(email)) {
					return true;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;

	}

	public User getLogin(String email, String password) throws SQLException {
		PreparedStatement ps = con.prepareStatement("select * from users where email = ? and password = ?");
		ps.setString(1, email);
		ps.setString(2, password);
		try (ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				return getUserRs(rs); // Hàm này xử lý logic để chuyển ResultSet thành User
			}
		}
		return null;
	}

	public static void main(String[] args) throws SQLException {
		UserDAO dao = new UserDAO(DBConnectionPool.getDataSource().getConnection());
		User s = dao.getLogin("admin@example.com", "123");
		System.out.println(s.toString());

	}

	public boolean checkUsername(String username) {
		String sql = "select username from users where username=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString("username").equals(username)) {
					return true;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;

	}

	public User findByEmail(String email) {
		try {
			PreparedStatement ps = con.prepareStatement("select * from users where email = ?");
			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return getUserRs(rs); // Hàm này xử lý logic để chuyển ResultSet thành User
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public User findById(int id) {
		try {
			PreparedStatement ps = con.prepareStatement("select * from users where id = ?");
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return getUserRs(rs); // Hàm này xử lý logic để chuyển ResultSet thành User
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public boolean changeImg(int id, String picPath) throws SQLException {
		String query = "UPDATE users SET images=? WHERE id=?";
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, picPath);
		ps.setInt(2, id);
		int row = ps.executeUpdate();
		return row > 0;
	}

	  // Lấy tất cả người dùng
	public List<User> getAllUsers() throws SQLException {
	    List<User> users = new ArrayList<>();
	    String sql = "SELECT * FROM users"; // Kiểm tra lại bảng và cột trong cơ sở dữ liệu

	    try (PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            users.add(getUserRs(rs));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    // Kiểm tra dữ liệu trả về
	    System.out.println("Danh sách người dùng từ DB: " + users);

	    return users;
	}



    // Xóa người dùng theo ID
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}