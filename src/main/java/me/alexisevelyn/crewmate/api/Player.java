package me.alexisevelyn.crewmate.api;

import me.alexisevelyn.crewmate.Server;
import me.alexisevelyn.crewmate.enums.DisconnectReason;
import me.alexisevelyn.crewmate.handlers.PlayerManager;
import me.alexisevelyn.crewmate.packethandler.packets.ClosePacket;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.security.SecureRandom;

public class Player extends Entity {
    private final String name;
    private final int id;
    private final InetAddress address;
    private final int port;
    private final int hazelVersion;
    private final int clientVersionRaw;
    private final Version clientVersion;
    private final Server server;

    private static final SecureRandom rnd = new SecureRandom();

    public Player(@NotNull String name, @NotNull InetAddress address, int port, int hazelVersion, int clientVersionRaw, @NotNull Server server) {
        super(4);
        this.name = name;
        int id = rnd.nextInt(server.getMaxPlayers());

        while (PlayerManager.existsWithID(id)) {
            id = rnd.nextInt(server.getMaxPlayers());
        }

        this.id = id;
        this.address = address;
        this.port = port;
        this.hazelVersion = hazelVersion;
        this.clientVersionRaw = clientVersionRaw;
        this.clientVersion = new Version(clientVersionRaw);
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    @NotNull
    public InetAddress getAddress() {
        return address;
    }

    public int getID() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Server getServer() {
        return server;
    }

    public int getClientVersionRaw() {
        return clientVersionRaw;
    }

    public int getHazelVersion() {
        return hazelVersion;
    }

    @NotNull
    public Version getClientVersion() {
        return clientVersion;
    }

    /*public void spawnEntity(Entity entity) throws IOException {
        int id = entity.getEntityID();
        int ownerId = getID();
        byte flags = (byte) (entity instanceof Player ? 1 : 0);
        byte[] components = new byte[]{};
        int componentCount = components.length;
        byte[] message = new byte[]{SendOption.RELIABLE.getByte(), 0x04, (byte) id, (byte) ownerId, flags, (byte) componentCount, 0x0};
        server.sendPacket(server.createSendPacket(address, port, message.length, message));
    }*/

    public void kick(@NotNull DisconnectReason reason) {
        byte[] message = ClosePacket.closeConnection(reason);
        server.sendPacket(server.createSendPacket(address, port, message.length, message));
    }

    public void kick(@NotNull String reason) {
        byte[] message = ClosePacket.closeWithMessage(reason);
        server.sendPacket(server.createSendPacket(address, port, message.length, message));
    }

    public static class Version {
        private static final char[] letters = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        private final long year;
        private final long month;
        private final long day;
        private final long revision;
        private final String revisionLetter;
        private final String revisionFull;
        private final String dateFull;
        private final String versionFull;

        public Version(int raw) {
            long clientVersion = raw;
            year = (long) Math.floor(clientVersion / 25000.0);
            clientVersion %= 25000;
            month = (long) Math.floor(clientVersion / 1800.0);
            clientVersion %= 1800;
            day = (long) Math.floor(clientVersion / 50.0);
            revision = clientVersion % 50;
            revisionLetter = (revision >= 0 && revision < letters.length) ? String.valueOf(letters[(int) revision]) : String.valueOf(revision);
            revisionFull = revision + revisionLetter;
            dateFull = "(" + day + "/" + month + "/" + year + ")";
            versionFull = revisionFull + " " + dateFull;
        }

        public long getDay() {
            return day;
        }

        public long getMonth() {
            return month;
        }

        public long getRevision() {
            return revision;
        }

        public long getYear() {
            return year;
        }

        @NotNull
        public String getDateFull() {
            return dateFull;
        }

        @NotNull
        public String getRevisionFull() {
            return revisionFull;
        }

        @NotNull
        public String getRevisionLetter() {
            return revisionLetter;
        }

        @NotNull
        public String getVersionFull() {
            return versionFull;
        }
    }

    /*
        TODO: Implement Code
        byte[] header = new byte[] {SendOption.NONE.getByte(), 0x00, 0x02, 0x0d, 0x00, 0x07};
	    return PacketHelper.mergeBytes(header, new byte[]{(byte) RPC.SNAP_TO.getRPC(), 5, 5});
     */
}
