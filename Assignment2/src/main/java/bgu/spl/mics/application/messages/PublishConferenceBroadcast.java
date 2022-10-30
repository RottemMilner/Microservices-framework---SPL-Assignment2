package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class PublishConferenceBroadcast implements Broadcast {

    int publication;

    public PublishConferenceBroadcast(int publication) {
        this.publication = publication;
    }

    public int getPublication() {
        return publication;
    }
}
