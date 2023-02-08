package com.gdczhl.saas.netty;

import java.util.List;

public class CmdRequest {

    //目标设备
    private List<String> targets;
    //json信息
    private String cmd;

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}