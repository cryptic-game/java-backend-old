package net.cryptic_game.backend.internal.network.invitation;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.internal.device.DeviceImpl;
import net.cryptic_game.backend.internal.network.Network;
import net.cryptic_game.backend.internal.network.NetworkWrapper;
import org.hibernate.Session;

public class InvitationWrapper {

    private static final SQLConnection sqlConnection;

    static {
        final AppBootstrap app = AppBootstrap.getInstance();
        sqlConnection = app.getSqlConnection();
    }

    public static Invitation inviteDevice(final Network network, final DeviceImpl device, final DeviceImpl inviter) {
        final Invitation invitation = new Invitation();
        invitation.setNetwork(network);
        invitation.setDevice(device);
        invitation.setRequest(false);
        invitation.setInviter(inviter);

        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        session.save(invitation);
        session.getTransaction().commit();
        session.close();
        return invitation;
    }

    public static Invitation requestNetwork(final Network network, final DeviceImpl device) {
        final Invitation invitation = new Invitation();
        invitation.setNetwork(network);
        invitation.setDevice(device);
        invitation.setRequest(true);
        invitation.setInviter(null);

        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        session.save(invitation);
        session.getTransaction().commit();
        session.close();
        return invitation;
    }

    public static void acceptInvitation(final Invitation invitation) {
        NetworkWrapper.addDevice(invitation.getNetwork(), invitation.getDevice());
        deleteInvitation(invitation);
    }

    public static void denyInvitation(final Invitation invitation) {
        deleteInvitation(invitation);
    }

    public static void deleteInvitation(final Invitation invitation) {
        final Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        sqlSession.delete(invitation);
        sqlSession.getTransaction().commit();
        sqlSession.close();
    }

    public static Invitation getInvitation(final Network network, final DeviceImpl device) {
        final Session sqlSession = sqlConnection.openSession();
        Invitation invitation = sqlSession.find(Invitation.class, new Invitation.InvitationKey(network, device));
        sqlSession.close();
        return invitation;
    }
}
