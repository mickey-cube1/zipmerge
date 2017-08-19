package uuid_999fcf00_a1e0_4c05_829b_d3288a7a4e71.java.zipmerge;

public class Getopt {
	/*
	 * Instance Variables
	 */
	protected String optstring;
	protected String[] argv;
	//
	protected String optarg;
	protected int optind = 0;
	protected int optopt = '?';
	protected int place = -1;

	public Getopt(String[] argv, String optstring)
	{
	  if (optstring.length() == 0){
	    optstring = " ";
	  }

	  this.argv = argv;
	  this.optstring = optstring;
	}

	public int getOptind()
	{
	  return optind;
	}

	public String getOptarg()
	{
	  return optarg;
	}

	public int getOptopt()
	{
	  return optopt;
	}

	public int getopt()
	{
		int oli = 0;

		if (place == -1)
        {
			place = 0;
			if (optind >= argv.length || argv[optind].charAt(place++) != '-')
            {
				place = -1;
				return -1;
            }
			if (place >= argv[optind].length())
            {
				place = -1;
				if (optstring.indexOf('-') == -1)
                {
					optopt = 0;
					return -1;
                }
				optopt = '-';
            }
			optopt = argv[optind].charAt(place++);
			if (optopt == '-' && place >= argv[optind].length())
            {
				optind++;
				place = -1;
				return -1;
            }
        }
		else
        {
			optopt = argv[optind].charAt(place++);
        }

		if (optopt == ':' || (oli = optstring.indexOf(optopt)) == -1)
        {
			if (place >= argv[optind].length())
            {
				optind++;
				place = -1;
            }
			return '?';
        }

		if (optstring.length() - 1 == oli || optstring.charAt(oli + 1) != ':')
        {
			/* no argument option */
			optarg = null;
			if (place >= argv[optind].length())
            {
				optind++;
				place = -1;
            }
        }
		else
        {
			/* option with an argument */
			if (place < argv[optind].length() - 1)
            {
				optarg = argv[optind].substring(place);
            }
			else if (argv.length > ++optind)
            {
				optarg = argv[optind];
            }
			else
            {
				place = -1;
				if (optstring.charAt(0) == ':')
                {
					return ':';
                }
				else
                {
					return '?';
                }
            }
			place = -1;
			optind++;
        }
		return optopt;
	}
}
/*
 * Getopt.java
 * zipmerge : copyright (c) 2017 micky-cube1.
 * This software is released under the MIT License.
 */