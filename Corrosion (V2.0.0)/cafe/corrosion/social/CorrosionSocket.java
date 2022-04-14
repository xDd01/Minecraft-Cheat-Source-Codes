/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social;

import cafe.corrosion.menu.social.FriendGUI;
import cafe.corrosion.menu.social.SocialGUI;
import cafe.corrosion.social.feature.Feature;
import cafe.corrosion.social.feature.impl.CommandFeature;
import cafe.corrosion.social.feature.impl.FriendsFeature;
import cafe.corrosion.social.feature.impl.NotificationFeature;
import cafe.corrosion.social.manager.FriendManager;
import cafe.corrosion.social.socket.WebSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CorrosionSocket {
    private FriendGUI friendGUI;
    private final SocialGUI socialGUI = new SocialGUI();
    private final FriendManager friendManager = new FriendManager();
    private final List<Feature> features = new ArrayList<Feature>();
    private final WebSocket webSocket = new WebSocket();

    public void init() {
        this.friendGUI = new FriendGUI();
        this.features.addAll(Arrays.asList(new FriendsFeature(this), new NotificationFeature(), new CommandFeature()));
        this.friendGUI.onReceiveFriendRequest();
    }

    public FriendGUI getFriendGUI() {
        return this.friendGUI;
    }

    public SocialGUI getSocialGUI() {
        return this.socialGUI;
    }

    public FriendManager getFriendManager() {
        return this.friendManager;
    }

    public List<Feature> getFeatures() {
        return this.features;
    }

    public WebSocket getWebSocket() {
        return this.webSocket;
    }
}

