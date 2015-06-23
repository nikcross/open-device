package org.onestonesoup.opendevice;

import org.onestonesoup.core.data.EntityTree;

public interface ArchiveListener {

	public void receiveArchive(Archivist archivist,EntityTree history);
}