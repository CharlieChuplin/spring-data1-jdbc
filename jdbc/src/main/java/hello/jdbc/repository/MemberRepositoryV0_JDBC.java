package hello.jdbc.repository;

import hello.jdbc.connenction.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0_JDBC {

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }

    public Member save(Member member) throws SQLException {

        String sql = "INSERT INTO MEMBER(MEMBER_ID, MONEY) VALUES (?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId()); //첫번째 파라미터 바인딩
            pstmt.setInt(2, member.getMoney());       //두번째 파라미터 바인딩
            pstmt.executeUpdate(); //반환값 : 영향 받은 DB ROW 수만큼 반환
            return member;
        } catch (SQLException e) {
            log.error("DB error ", e);
            throw e;
        } finally {
//            pstmt.close(); // Exception이 발생한다면...
//            con.close();   // 호출되지 않는다...
            close(con, pstmt, null);
        }

    }

    public Member findById(String memberId) throws SQLException {

        String sql = "SELECT * FROM MEMBER WHERE MEMBER_ID = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        } catch (SQLException e) {
            log.error("DB error ", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }

    }

    public void update(String memberId, int money) throws SQLException {

        String sql = "UPDATE MEMBER SET MONEY=? WHERE MEMBER_ID=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("DB error ", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    public void delete(String memberId) throws SQLException {

        String sql = "DELETE FROM MEMBER WHERE MEMBER_ID=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("DB error ", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    /**
     * finally 구문에서 pstmt.close(); 에서 오류가 난다면 뒤의 con.close() 는 호출되지 않는다.
     * 전부 try/catch 로 구현해야 하는데 코드가 너무 지저분해져 method로 따로 빼놓아서 구현.
     */
    private void close(Connection con, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("error ", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("error ", e);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.error("error ", e);
            }
        }

    }


}
