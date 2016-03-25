package com.rubic.sso.po;

/**
 * Created by LiuMian on 2016/2/28.
 */
public class User {

    private String user_name;
    private int user_id;
    private String password;
    private String email;
	private int state;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	@Override
	public String toString() {
		return "User{" +
				"user_name='" + user_name + '\'' +
				", user_id=" + user_id +
				", password='" + password + '\'' +
				", email='" + email + '\'' +
				", state=" + state +
				'}';
	}
}
