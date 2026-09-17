package com.deathmotion.marlowcrystal.network;

import com.deathmotion.marlowcrystal.network.packet.ChallengeResponsePacket;
import com.deathmotion.marlowcrystal.network.packet.OptOutAckPacket;
import com.deathmotion.marlowcrystal.network.packet.VersionPacket;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public final class ServerSession {

    private volatile boolean optedOut;

    private volatile boolean notified;

    public boolean isOptedOut() {
        return optedOut;
    }

    public void joined(Minecraft client, Consumer<ServerboundPacket> send) {
        if (client.isLocalServer()) return;

        send.accept(VersionPacket.current());
    }

    public void optOutReceived(Minecraft client, Consumer<ServerboundPacket> send) {
        optedOut = true;

        if (claimNotice()) {
            OptOutNotice.showLater(client);
        }

        send.accept(OptOutAckPacket.INSTANCE);
    }

    public void challengeReceived(int challengeId, Consumer<ServerboundPacket> send) {
        send.accept(new ChallengeResponsePacket(challengeId));
    }

    public synchronized void disconnected() {
        optedOut = false;
        notified = false;
    }

    private synchronized boolean claimNotice() {
        if (notified) return false;

        notified = true;
        return true;
    }
}
