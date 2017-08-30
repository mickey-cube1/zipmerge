package uuid_999fcf00_a1e0_4c05_829b_d3288a7a4e71.java.zipmerge;

import java.util.zip.ZipEntry;

public class ZipEntryEx {
	public ZipEntry zentry;
	public int findex;
	public boolean ignorecasef;
	public String cname;

	public ZipEntryEx(String aCname, ZipEntry az, int ai, boolean ignf) {
		this.cname = aCname;
		this.zentry = az;
		this.findex = ai;
		this.ignorecasef = ignf;
	}

	public boolean equals(Object o) {
		ZipEntryEx oe = (ZipEntryEx) o;
		String an = cname;
		String bn = oe.cname;
		if (ignorecasef || oe.ignorecasef) {
			return an.equalsIgnoreCase(bn);
		} else {
			return an.equals(bn);
		}
	}
}
/*
 * ZipEntryEx.java zipmerge : copyright (c) 2017 micky-cube1. This software is
 * released under the MIT License.
 */