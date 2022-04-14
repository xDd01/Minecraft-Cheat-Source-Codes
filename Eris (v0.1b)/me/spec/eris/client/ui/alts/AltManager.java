package me.spec.eris.client.ui.alts;

import me.spec.eris.api.manager.Manager;

public class AltManager extends Manager<Alt> {


    public void addAlt(Alt alt) {
        getManagerArraylist().add(alt);
    }

    public void removeAlt(Alt alt) {
        if (getManagerArraylist().contains(alt)) {
            getManagerArraylist().remove(alt);
        }
    }

    public void removeAlt(int alt) {
        if (getManagerArraylist().size() > alt) {
            getManagerArraylist().remove(alt);
        }
    }

    public Alt getAltByName(String name) {
        for(Alt alt : getManagerArraylist()) {
            if(alt.getUser().equalsIgnoreCase(name)) {
                return alt;
            }
        }
        return new Alt("lmao", "lmao");
    }
}
