package net.cryptic_game.backend.data.network.member;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import org.hibernate.Session;

public class MemberWrapper {

  private static final SQLConnection sqlConnection;

  static {
    final AppBootstrap app = AppBootstrap.getInstance();
    sqlConnection = app.getSqlConnection();
  }

  public static Member createMembership(final Network network, final Device device) {
    Member existingMember = getMembership(network, device);
    if (existingMember != null) {
      return existingMember;
    }

    final Member member = new Member();
    member.setNetwork(network);
    member.setDevice(device);

    final Session sqlSession = sqlConnection.openSession();
    sqlSession.beginTransaction();
    sqlSession.save(member);
    sqlSession.getTransaction().commit();
    sqlSession.close();
    return member;
  }

  public static Member getMembership(final Network network, final Device device) {
    final Session sqlSession = sqlConnection.openSession();
    Member member = sqlSession.find(Member.class, new Member.MemberKey(network, device));
    sqlSession.close();
    return member;
  }
}
