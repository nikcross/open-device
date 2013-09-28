package org.one.stone.soup.open.device;

import org.one.stone.soup.core.data.EntityTree;

public interface ArchiveListener {

	public void receiveArchive(Archivist archivist,EntityTree history);
}