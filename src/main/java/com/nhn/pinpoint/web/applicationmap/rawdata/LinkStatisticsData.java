package com.nhn.pinpoint.web.applicationmap.rawdata;

import java.util.*;

import com.nhn.pinpoint.common.bo.AgentInfoBo;
import com.nhn.pinpoint.web.vo.Application;
import com.nhn.pinpoint.web.vo.LinkKey;

public class LinkStatisticsData {

    private final Map<LinkKey, LinkStatistics> linkStatData = new HashMap<LinkKey, LinkStatistics>();

	public LinkStatisticsData() {
	}

    public Collection<LinkStatistics> getLinkStatData() {
        return linkStatData.values();
    }

    public void addLinkData(Application srcApplication, String srcAgentId, Application destApplication, String destAgentId, long timestamp, short slotTime, long value) {
        final LinkStatistics linkStat = getLinkStatistics(srcApplication, destApplication);
        linkStat.addCallData(srcAgentId, srcApplication.getServiceTypeCode(), destAgentId, destApplication.getServiceTypeCode(), timestamp, slotTime, value);
    }

    public Map<Application, Set<AgentInfoBo>> getAgentMap() {
		final Map<Application, Set<AgentInfoBo>> agentMap = new HashMap<Application, Set<AgentInfoBo>>();
		for (LinkStatistics stat : linkStatData.values()) {
			if (stat.getToAgentSet() == null) {
				continue;
			}
            Application key = stat.getToApplication();
            final Set<AgentInfoBo> agentInfoBos = agentMap.get(key);
            if (agentInfoBos != null) {
				Set<AgentInfoBo> toAgentSet = stat.getToAgentSet();
				if (toAgentSet != null) {
					agentInfoBos.addAll(stat.getToAgentSet());
				}
			} else {
				agentMap.put(key, stat.getToAgentSet());
			}
		}
		return agentMap;
	}

	@Override
	public String toString() {
		return "LinkStatisticsData [linkStatData=" + linkStatData + "]";
	}

    public void addLinkStatisticsData(LinkStatisticsData linkStatisticsData) {
        if (linkStatisticsData == null) {
            throw new NullPointerException("linkStatisticsData must not be null");
        }
        for (LinkStatistics copyLinkStatistics : linkStatisticsData.linkStatData.values()) {
            addLinkStatistics(copyLinkStatistics);
        }
    }

    public void addLinkStatistics(LinkStatistics copyLinkStatistics) {
        if (copyLinkStatistics == null) {
            throw new NullPointerException("copyLinkStatistics must not be null");
        }
        Application fromApplication = copyLinkStatistics.getFromApplication();
        Application toApplication = copyLinkStatistics.getToApplication();
        LinkStatistics linkStatistics = getLinkStatistics(fromApplication, toApplication);
        linkStatistics.add(copyLinkStatistics);
    }

    private LinkStatistics getLinkStatistics(Application fromApplication, Application toApplication) {
        final LinkKey key = new LinkKey(fromApplication, toApplication);
        LinkStatistics findLink = linkStatData.get(key);
        if (findLink == null) {
            findLink = new LinkStatistics(fromApplication, toApplication);
            linkStatData.put(key, findLink);
        }
        return findLink;
    }

    public int size() {
        return linkStatData.size();
    }
}
