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
			System.out.println("the_simple");
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
	final static int[] dy = {-1, 0, 0, 1, 0};
	final static int[] dx = {0, -1, 1, 0, 0};
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
	
	int[][] itemDist = new int[H][W];
	int[] item = new int[20];
	int items;
	int[][] dogDist = new int[H][W];
	int[] dog = new int[H*W];
	int dogs;
	
	int[] pos = new int[2];
	
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
	
	void addItem(int y, int x, int i){item[i] = y*W+x;}
	void addDog(int y, int x, int i){dog[i] = y*W+x;}
	
	String setSkill;
	String think(ContestScanner sc) throws IOException {
//		StringBuilder res = new StringBuilder();
		long millitime = sc.nextLong();
		setSkill = null;
		sc.nextInt();
		Arrays.fill(map, 0);
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
					if(s[j]=='O') setStone(i, j, map);
					else if(s[j]=='W') setWall(i, j, map);
				}
			}

			// character
			n = sc.nextInt();
			int rows[] = new int[n];
			int cols[] = new int[n];
			for (int i = 0; i < n; ++i) {
				int id = sc.nextInt(), row = sc.nextInt(), col = sc.nextInt();
				pos[id] = row*W+col;
				if(id==0) setNinja1(row, col, map);
				else setNinja2(row, col, map);
			}
			// zombie
			n = sc.nextInt();
			dogs = n;
			for (int i = 0; i < n; ++i) {
				int id = sc.nextInt(), row = sc.nextInt(), col = sc.nextInt();
				setDog(row, col, id, map);
				addDog(row, col, i);
			}
			// item
			n = sc.nextInt();
			items = n;
			for (int i = 0; i < n; ++i) {
				int row = sc.nextInt(), col = sc.nextInt();
				setItem(row, col, map);
				addItem(row, col, i);
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
		order();
		String res = "";
		for(int i=0; i<2; i++) res += walkEachSimple(i)+"\n";
		if(setSkill != null) res = "3\n" + setSkill + "\n" + res;
		else res = "2\n" + res;
		return res;
	}
	
	
	String walkEachSimple(int id){
		int bestDog = 0;
		int bestItem = Integer.MAX_VALUE; // item優先
		int bm1 = 4, bm2 = 4;
		boolean isItem = false;
		final int y = pos[id]/W;
		final int x = pos[id]%W;
//		System.err.println(id+":pos:"+y+","+x);
//		dump(itemDist);
		int dogCount = 0;
		int stoneCount = 0;
		for(int i=0; i<4; i++){
			if(!okMove(y, x, i) || dogDist[y+dy[i]][x+dx[i]] == 0) continue;
			final int ny = y+dy[i];
			final int nx = x+dx[i];
			if(isDog(ny, nx, map)) dogCount++;
			else if(isStone(ny, nx, map)) stoneCount++;
			if(isItem(ny, nx, map)) isItem = true;
			for(int j=0; j<4; j++){
				if(!okMove(ny, nx, j) || dogDist[ny+dy[j]][nx+dx[j]] <= 1
						|| x==nx+dx[j]&&y==ny+dy[j]) continue;
				int point = itemDist[ny+dy[j]][nx+dx[j]];
				if(isItem){
					point = point==0?-2:-1;
					isItem = false;
				}
				if(bestItem>point){
					bestItem = point;
					// bestDog = dogDist[ny][nx] // 今は犬(の距離)は無視
					bm1 = i;
					bm2 = j;
				}
			}
		}
		if(bm1==4 || dogCount+stoneCount==4 && dogCount>1){
			// (術を使わなければ)詰み
			setSkill = "7 "+id;
		}
		return ds[bm1]+ds[bm2];
	}
	
	void dump(int[][] dist){
		for(int i=0; i<dist.length; i++){
			for(int j=0; j<dist[i].length; j++){
				System.out.print(dist[i][j]+"\t");
			}
			System.out.println();
		}
	}
	
	boolean okMove(int y, int x, int d){
		final int ny = y+dy[d];
		final int nx = x+dx[d];
		if(isFloor(ny, nx, map)) return true;
		if(!isStone(ny, nx, map)) return false;
		return isStoneMove(ny+dy[d], nx+dx[d], map);
	}
	
//	int dist[][] = new int[H][W];
	void order() {
		bfs(itemDist, item, items);
		bfs(dogDist, dog, dogs);
	}

	int qy[] = new int[H*W], qx[] = new int[H*W];
	void bfs(int[][] dist, int[] list, int n){
		for (int i = 0; i < H; ++i)
			Arrays.fill(dist[i], Integer.MAX_VALUE);
		int qi = 0, qe = 0;
		for(int i=0; i<n; i++){
			final int y = list[i]/W;
			final int x = list[i]%W;
			if(isStone(y, x, map)) continue;
			qy[i] = y;
			qx[i] = x;
			dist[y][x] = 0;
			qe++;
		}
		while (qi < qe) {
			int y = qy[qi], x = qx[qi];
			++qi;
			for (int i = 0; i < 4; ++i) {
				int ny = y+dy[i];
				int nx = x+dx[i];
				if (isFloor(ny, nx, map) && dist[ny][nx] == Integer.MAX_VALUE) {
					dist[ny][nx] = dist[y][x] + 1;
					qy[qe] = ny;
					qx[qe] = nx;
					++qe;
				}else if(isStone(ny, nx, map) && dist[ny][nx]==Integer.MAX_VALUE)
					dist[ny][nx] = dist[y][x] + 1;
			}
		}
	}
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
