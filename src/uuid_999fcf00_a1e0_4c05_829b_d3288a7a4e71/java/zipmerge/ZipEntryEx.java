package uuid_999fcf00_a1e0_4c05_829b_d3288a7a4e71.java.zipmerge;

import java.util.zip.ZipEntry;

public class ZipEntryEx {
	public ZipEntry zentry;
	public int findex;
	public ZipEntryEx(ZipEntry az, int ai){
		this.zentry = az;
		this.findex = ai;
	}
	public boolean equals(Object o) {
		ZipEntryEx oe = (ZipEntryEx)o;
		return zentry.getName().equals(oe.zentry.getName());
	}
}
/*
 * ZipEntryEx.java
 * zipmerge : copyright (c) 2017 micky-cube1.
 * This software is released under the MIT License.
 */