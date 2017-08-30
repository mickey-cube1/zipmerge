package uuid_999fcf00_a1e0_4c05_829b_d3288a7a4e71.java.zipmerge;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipMerge {
	private static int FLAG_ALL_YES = 0x01;
	private static int FLAG_ALL_NO = 0x02;
	private static int FLAG_SAME_YES = 0x04;
	private static int FLAG_SAME_NO = 0x08;

	// 4MB buffer
	private final byte[] BUFFER = new byte[4096 * 1024];
	private int verboselevel;
	private int confirmflags;
	private boolean nameicaseflag;
	private boolean ignoredirpartflag;

	// copy input to output stream
	public void copy(InputStream input, OutputStream output) throws IOException {
		int bytesRead;
		while ((bytesRead = input.read(BUFFER)) != -1) {
			output.write(BUFFER, 0, bytesRead);
		}
	}

	/*
	 * @param outzippath
	 * 
	 * @param inzippath
	 */
	public ZipMerge(int vlevel, int cmode, boolean nif, boolean sdf) {
		this.verboselevel = vlevel;
		this.confirmflags = cmode;
		this.nameicaseflag = nif;
		this.ignoredirpartflag = sdf;
	}

	/*
	 * @param outzippath
	 * 
	 * @param inzippath
	 */
	private int confirmReplace(ZipEntryEx za, String sa, ZipEntryEx zb,
			String sb) {
		Date da = new Date(za.zentry.getTime());
		Date db = new Date(zb.zentry.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if ((confirmflags & FLAG_ALL_YES) != 0) {
			return 1;
		} else if ((confirmflags & FLAG_ALL_NO) != 0) {
			return 0;
		}

		if (za.zentry.getSize() == zb.zentry.getSize()
				&& za.zentry.getCrc() == zb.zentry.getCrc()) {
			if ((confirmflags & FLAG_SAME_YES) != 0) {
				return 1;
			} else if ((confirmflags & FLAG_SAME_NO) != 0) {
				return 0;
			}
		}

		System.out
				.format("replace '%s':\n     %s %08x %8d  in  '%s'\nwith %s %08x %8d from '%s'? ",
						za.zentry.getName(), sdf.format(da),
						za.zentry.getCrc(), za.zentry.getSize(), sa,
						sdf.format(db), zb.zentry.getCrc(),
						zb.zentry.getSize(), sb);
		System.out.flush();

		Scanner sc = new Scanner(System.in);
		String line = sc.nextLine();
		if (line.length() == 0) {
			return 0;
		}
		char c = line.charAt(0);

		if (c == 'y' || c == 'Y') {
			return 1;
		}

		return 0;
	}

	/*
	 * @param outzippath
	 * 
	 * @param inzippath
	 */
	public void merge(String outzippath, String[] inzippath) throws IOException {
		int i;
		ZipFile inZip;
		ZipEntryEx inEntry;
		ArrayList<ZipEntryEx> zea = new ArrayList<ZipEntryEx>(100);
		ArrayList<ZipFile> zfa = new ArrayList<ZipFile>(inzippath.length);
		printverbose(1, "Dst: " + outzippath);
		ZipOutputStream moddedZip = new ZipOutputStream(new FileOutputStream(
				outzippath));

		for (i = 0; i < inzippath.length; i++) {
			printverbose(1, "Src" + i + ": " + inzippath[i]);
			inZip = new ZipFile(inzippath[i]);
			zfa.add(inZip);
			//
			Enumeration<? extends ZipEntry> entries = inZip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry e = entries.nextElement();

				String cname = e.getName();
				if (ignoredirpartflag){
					File tmpf = new File(cname);
					cname = tmpf.getName();
				}
				
				inEntry = new ZipEntryEx(cname, e, i, nameicaseflag);
				int oi = zea.indexOf(inEntry);
				if (oi == -1) {
					zea.add(inEntry);
				} else {
					ZipEntryEx oe = zea.get(oi);
					int chk = confirmReplace(oe, inzippath[oe.findex], inEntry,
							inzippath[i]);
					if (chk != 0) {
						zea.remove(oi);
						zea.add(inEntry);
					}
				}
			}
		}
		for (i = 0; i < zea.size(); i++) {
			ZipEntryEx eex = zea.get(i);
			ZipEntry e = eex.zentry;
			int sfi = eex.findex;
			String name = e.getName();
			printverbose(1, "copy: " + name + " from " + inzippath[sfi]);
			moddedZip.putNextEntry(e);
			if (!e.isDirectory()) {
				copy(zfa.get(sfi).getInputStream(e), moddedZip);
			}
			moddedZip.closeEntry();
		}
		for (i = 0; i < zfa.size(); i++) {
			printverbose(2, "Close Src" + i + ": " + inzippath[i]);
			zfa.get(i).close();
		}

		moddedZip.close();
	}

	private static void showusage() {
		System.out
				.print("zipmerge by micky-cube1\n\n"
						+ "usage: java -jar zipmerge,jar [-DhIiSsVv] target-zip zip...\n"
						+ "\n"
						+ "  -h       display this help message\n"
						+ "  -V       display version number\n"
						+ "  -D       ignore directory component in file names\n"
						+ "  -I       ignore case in file names\n"
						+ "  -i       ask before overwriting files\n"
						+ "  -S       don't overwrite identical files\n"
						+ "  -s       overwrite identical files without asking\n"
						+ "  -v       verbose mode\n" + "\n"
						+ "Report bugs to <micky.cube1+github@gmail.com>.\n");
	}

	private void printverbose(int vlevel, String msg) {
		if (verboselevel >= vlevel) {
			System.err.format("debug%d: %s\n", vlevel, msg);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int optind;
		int c;
		Getopt getopt = new Getopt(args, "hVDiIsSv");
		int confirmflags = FLAG_ALL_YES;
		boolean nameicaseflag = false;
		boolean skipdirflag = false;
		int verboselv = 0;

		while ((c = getopt.getopt()) != -1) {
			switch (c) {
			case 'D':
				skipdirflag = true;
				break;
			case 'i':
				confirmflags &= ~FLAG_ALL_YES;
				break;
			case 'I':
				nameicaseflag = true;
				break;
			case 's':
				confirmflags &= ~FLAG_SAME_NO;
				confirmflags |= FLAG_SAME_YES;
				break;
			case 'S':
				confirmflags &= ~FLAG_SAME_YES;
				confirmflags |= FLAG_SAME_NO;
				break;

			case 'h':
				showusage();
				return;
			case 'V':
				System.out
						.print("zipmerge version 0.0.20170814\n"
								+ "Copyright (C) 2017 micky-cube1\n"
								+ "zipmerge comes with ABSOLUTELY NO WARRANTY, to the extent permitted by law.\n");
				return;

			case 'v':
				verboselv++;
				break;

			default:
				showusage();
				return;
			}
		}

		optind = getopt.getOptind();
		if (args.length < optind + 2) {
			showusage();
			return;
		}

		//
		String path = args[optind];
		String[] srczips = java.util.Arrays.copyOfRange(args, optind + 1,
				args.length);
		try {
			ZipMerge zm = new ZipMerge(verboselv, confirmflags, nameicaseflag, skipdirflag);
			zm.merge(path, srczips);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return;
	}
}
/*
 * ZipMerge.java zipmerge : copyright (c) 2017 micky-cube1. This software is
 * released under the MIT License.
 */