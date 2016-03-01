import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {
	public static final void main(String args[]) {
		try {
			new Main().solve();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void solve() throws IOException {
		try (ContestScanner sc = new ContestScanner()) {
			System.out.println("SampleAI.java");
			System.out.flush();
			while (true) {
				System.out.print(think(sc));
				System.out.flush();
			}
		}
	}

//	private static final int dx[] = { 0, 1, 0, -1 };
//	private static final int dy[] = { 1, 0, -1, 0 };
//	private static final String ds[] = { "L", "U", "R", "D" };
//	int point, map_row, map_col;
//	boolean[][] map, itemMap;
	final static int H = 17;
	final static int W = 14;
	int[] map = new int[H*W];
	final static int[] dx = {-1, 0, 0, 1, 0};
	final static int[] dy = {0, -1, 1, 0, 0};
	final static String[] ds = {"U", "L", "R", "D", "N"};
	int pow;
	final static int skills = 8; 
	int[] cost = new int[8];
	final static int shd = 10;
	final static int msd = (1<<shd)-1;
	final static int shn1 = 11;
	final static int msn1 = 1<<shn1;
	final static int shn2 = 12;
	final static int msn2 = 1<<shn2;
	final static int shw = 13;
	final static int msw = 1<<shw;
	final static int shs = 14;
	final static int mss = 1<<shs;
	final static int shi = 15;
	final static int msi = 1<<shi;
	final static int msEmpty = (1<<15)-1;
	
	void setStone(int y, int x, int[] s){s[y*W+x] |= mss;}
	void setWall(int y, int x, int[] s){s[y*W+x] |= msw;}
	void setNinja1(int y, int x, int[] s){s[y*W+x] |= msn1;}
	void setNinja2(int y, int x, int[] s){s[y*W+x] |= msn2;}
	void setItem(int y, int x, int[] s){s[y*W+x] |= msi;}
	void setDog(int y, int x, int id, int[] s){s[y*W+x] |= id;}
	
	boolean isStone(int y, int x, int[] s){return (s[y*W+x]&mss)>0;}
	boolean isWall(int y, int x, int[] s){return (s[y*W+x]&msw)>0;}
	boolean isFloor(int y, int x, int[] s){return (s[y*W+x]&(mss|msw))==0;}
	boolean isStoneMove(int y, int x, int[] s){return (s[y*W+x]&msEmpty)==0;}
	boolean isNinja(int y, int x, int[] s){return (s[y*W+x]&(msn1|msn2))>0;}
	boolean isItem(int y, int x, int[] s){return (s[y*W+x]&msi)>0;}
	boolean isDog(int y, int x, int[] s){return (s[y*W+x]&msd)>0;}

	String think(ContestScanner sc) throws IOException {
		StringBuilder res = new StringBuilder();
		long millitime = sc.nextLong();
		sc.nextInt();
		for (int i = 0; i < skills; ++i) {
			cost[i] = sc.nextInt();
		}
		{
			pow = sc.nextInt();
			sc.nextInt();
			sc.nextInt();
			int n;
			for(int i=0; i<H; i++){
				char[] s = sc.nextToken().toCharArray();
				for(int j=0; j<W; j++){
					if(s[i]=='O') setStone(i, j, map);
					else if(s[i]=='W') setWall(i, j, map);
				}
			}

			// character
			n = sc.nextInt();
			int rows[] = new int[n];
			int cols[] = new int[n];
			for (int i = 0; i < n; ++i) {
				int id = sc.nextInt(), row = sc.nextInt(), col = sc.nextInt();
				if(id==0) setNinja1(row, col, map);
				else setNinja2(row, col, map);
			}
			// zombie
			n = sc.nextInt();
			for (int i = 0; i < n; ++i) {
				int id = sc.nextInt(), row = sc.nextInt(), col = sc.nextInt();
				setDog(row, col, id, map);
			}
			// item
			n = sc.nextInt();
			for (int i = 0; i < n; ++i) {
				int row = sc.nextInt(), col = sc.nextInt();
				setItem(row, col, map);
			}
			int useSkill[] = new int[skills];
			for (int i = 0; i < skills; ++i) {
				useSkill[i] = sc.nextInt();
			}
//			res.append(rows.length).append("\n");
//			for (int i = 0; i < rows.length; ++i) {
//				res.append(order(rows[i], cols[i])).append("\n");
//			}
		}
		{
			int point = sc.nextInt(), map_row = sc.nextInt(), map_col = sc.nextInt();
			boolean map[][] = new boolean[map_row][map_col];
			for (int r = 0; r < map_row; ++r) {
				String line = sc.nextToken();
				for (int c = 0; c < map_col; ++c) {
					map[r][c] = line.charAt(c) == '_';
				}
			}
			for (int i = 0, n = sc.nextInt(); i < n; ++i) {
				int id = sc.nextInt(), row = sc.nextInt(), col = sc.nextInt();
			}
			for (int i = 0, n = sc.nextInt(); i < n; ++i) {
				int id = sc.nextInt(), row = sc.nextInt(), col = sc.nextInt();
			}
			for (int i = 0, n = sc.nextInt(); i < n; ++i) {
				int row = sc.nextInt(), col = sc.nextInt();
			}
			for (int i = 0; i < skills; ++i) {
				int use = sc.nextInt();
			}
		}
		return res.toString();
	}
	
	boolean okMove(int y, int x, int d){
		final int ny = y+dy[d];
		final int nx = x+dx[d];
		if(isFloor(ny, nx, map)) return true;
		if(!isStone(ny, nx, map)) return false;
		return isStoneMove(ny+dy[d], nx+dx[d], map);
	}
	

//	String order(final int row, final int col) {
//		int dist[][] = new int[map_row][map_col];
//		for (int i = 0; i < dist.length; ++i)
//			Arrays.fill(dist[i], Integer.MAX_VALUE);
//		dist[row][col] = 0;
//		int qr[] = new int[map_row * map_col], qc[] = new int[map_row * map_col], qi = 0, qe = 1;
//		qr[0] = row;
//		qc[0] = col;
//		while (qi < qe) {
//			int r = qr[qi], c = qc[qi];
//			++qi;
//			for (int i = 0; i < 4; ++i) {
//				int nr = r + dx[i], nc = c + dy[i];
//				if (map[nr][nc] && dist[nr][nc] == Integer.MAX_VALUE) {
//					dist[nr][nc] = dist[r][c] + 1;
//					qr[qe] = nr;
//					qc[qe] = nc;
//					++qe;
//				}
//			}
//		}
//
//		int tr = -1, tc = -1, tdist = Integer.MAX_VALUE;
//		for (int r = 0; r < map_row; ++r) {
//			for (int c = 0; c < map_col; ++c) {
//				if (itemMap[r][c] && tdist > dist[r][c]) {
//					tdist = dist[r][c];
//					tr = r;
//					tc = c;
//				}
//			}
//		}
//
//		StringBuilder res = new StringBuilder();
//		if (tdist != Integer.MAX_VALUE) {
//			while (dist[tr][tc] > 0) {
//				for (int i = 0; i < 4; ++i) {
//					int nr = tr + dx[i], nc = tc + dy[i];
//					if (dist[tr][tc] > dist[nr][nc]) {
//						tr = nr;
//						tc = nc;
//						res.append(ds[i]);
//						break;
//					}
//				}
//			}
//		}
//		String s = res.reverse().toString();
//		return s.length() <= 2 ? s : s.substring(0, 2);
//	}
}

class ContestScanner implements AutoCloseable{
	private InputStreamReader in;private int c=-2;
	public ContestScanner()throws IOException 
	{in=new InputStreamReader(System.in);}
	public ContestScanner(String filename)throws IOException
	{in=new InputStreamReader(new FileInputStream(filename));}
	public String nextToken()throws IOException {
		StringBuilder sb=new StringBuilder();
		while((c=in.read())!=-1&&Character.isWhitespace(c));
		while(c!=-1&&!Character.isWhitespace(c)){sb.append((char)c);c=in.read();}
		return sb.toString();
	}
	public String readLine()throws IOException{
		StringBuilder sb=new StringBuilder();if(c==-2)c=in.read();
		while(c!=-1&&c!='\n'&&c!='\r'){sb.append((char)c);c=in.read();}
		return sb.toString();
	}
	public long nextLong()throws IOException,NumberFormatException
	{return Long.parseLong(nextToken());}
	public int nextInt()throws NumberFormatException,IOException
	{return(int)nextLong();}
	public double nextDouble()throws NumberFormatException,IOException 
	{return Double.parseDouble(nextToken());}
	@Override
	public void close() throws IOException{
		in.close();
	}
}
