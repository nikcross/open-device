package org.onestonesoup.opendevice;

import org.one.stone.soup.core.data.EntityTree;

public interface ArchiveListener {

	public void receiveArchive(Archivist archivist,EntityTree history);
}