package bgu.spl.mics.application.objects;

import bgu.spl.mics.MicroService;

public class MicroServiceTestExample extends MicroService
{
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public MicroServiceTestExample(String name) {
        super(name);
    }

    @Override
    protected void initialize() {

    }
}
