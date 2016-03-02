import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

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
	int turn = 0;
	final static int H = 17;
	final static int W = 14;
	int[] map = new int[H*W];
	int[] submap = new int[H*W];
	final static int[] dy = {-1, 0, 0, 1, 0};
	final static int[] dx = {0, -1, 1, 0, 0};
	final static int[] rdir = {3, 2, 1, 0, 4};
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
	final static int inf = 100000;
	
	final static int SK_FAST = 0;
	final static int SK_STONE_ME = 1;
	final static int SK_STONE_EN = 2;
	final static int SK_THUND_ME = 3;
	final static int SK_THUND_EN = 4;
	final static int SK_COPY_ME = 5;
	final static int SK_COPY_EN = 6;
	final static int SK_ATTACK = 7;
	
	HashMap<Integer, Integer> xy2idxItem = new HashMap<>();
	int[][] itemDist = new int[H][W];
	int[] item = new int[20];
	int[] subitem = new int[20];
	int items;
	int[][] dogDist = new int[H][W];
	int[] dog = new int[H*W];
	int dogs;
	
	int[] pos = new int[2];
	int[] target = new int[2];
	int[] targetDist = new int[2];
	int[] subpos = new int[2];
	
	void setStone(int y, int x, int[] s){s[y*W+x] |= mss;}
	void setWall(int y, int x, int[] s){s[y*W+x] |= msw;}
	void setNinja1(int y, int x, int[] s){s[y*W+x] |= msn1;}
	void setNinja2(int y, int x, int[] s){s[y*W+x] |= msn2;}
	void setNinja(int y, int x, int id,int[]s){s[y*W+x]|=msn1<<id;pos[id]=y*W+x;}
	void setItem(int y, int x, int[] s){s[y*W+x] |= msi;}
	void setDog(int y, int x, int id, int[] s){s[y*W+x] |= id;}
	
	boolean isStone(int y, int x, int[] s){return (s[y*W+x]&mss)>0;}
	boolean isWall(int y, int x, int[] s){return (s[y*W+x]&msw)>0;}
	boolean isFloor(int y, int x, int[] s){return (s[y*W+x]&(mss|msw))==0;}
	boolean isStoneMove(int y, int x, int[] s){return (s[y*W+x]&msEmpty)==0;}
	boolean isNinja(int y, int x, int[] s){return (s[y*W+x]&(msn1|msn2))>0;}
	boolean isItem(int y, int x, int[] s){return (s[y*W+x]&msi)>0;}
	boolean isDog(int y, int x, int[] s){return (s[y*W+x]&msd)>0;}
	
	void removeStone(int y, int x, int[] s){s[y*W+x] &= ~mss;}
	void removeNinja(int y, int x, int id, int[] s){s[y*W+x]&=~(msn1<<id);}
	
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
				subpos[id] = pos[id];
				setNinja(row, col, id, map);
			}
			// zombie
			n = sc.nextInt();
			dogs = n;
			for (int i = 0; i < n; ++i) {
				int id = sc.nextInt(), row = sc.nextInt(), col = sc.nextInt();
				System.err.println(id+", "+row+", "+col);
				setDog(row, col, id+1, map);
				addDog(row, col, i);
			}
			// item
			n = sc.nextInt();
			items = n;
			xy2idxItem.clear();
			for (int i = 0; i < n; ++i) {
				int row = sc.nextInt(), col = sc.nextInt();
				xy2idxItem.put(row*W+col, i);
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
			for(int i=0; i<items; i++) subitem[i] = item[i];
			for(int i=0; i<map.length; i++) submap[i] = map[i];
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
//		if(turn==3){
//			System.err.println("turn");
//		}
		System.err.println("turn:"+turn);
		if(turn==32){
			System.err.println("stop");
		}
		order();
		String res = "";
		for(int i=0; i<2; i++) res += searchItemSimple(i)+"\n";
		if(setSkill != null) res = "3\n" + setSkill + "\n" + res;
		else res = "2\n" + res;
		turn++;
		return res;
	}
	
	void update(){
		// 確定した分を更新
		
	}
	
	void back(){
		// マップ、プレイヤー座標、アイテム を戻す
		// 今後、スキルなどもありうる
	}
	
	String walkEachSimple(int pid){
		int bestDog = 0;
		int bestItem = inf; // item蜆ｪ蜈�
		int bm1 = 4, bm2 = 4, bm3 = -1;
		boolean isItem = false;
		final int y = pos[pid]/W;
		final int x = pos[pid]%W;
//		System.err.println(id+":pos:"+y+","+x);
//		dump(itemDist);
//		System.out.println("Dog:");
//		dump(map, msd);
//		System.out.println("Stone:");
//		dump(map, mss);
		boolean useFast = pow>cost[SK_ATTACK];
		int dogCount = 0;
		int stoneCount = 0;
		for(int i=0; i<4; i++){
			if(!okMove(y, x, i) || dogDist[y+dy[i]][x+dx[i]] == 0) continue;
			final int ny = y+dy[i];
			final int nx = x+dx[i];
			if(isDog(ny, nx, map)) dogCount++;
			else if(isStone(ny, nx, map)) stoneCount++;
			if(isItem(ny, nx, map)) isItem = true;
			for(int j=0; j<5; j++){
				if(!okMove(ny, nx, j) || dogDist[ny+dy[j]][nx+dx[j]] <= 1
						|| x==nx+dx[j]&&y==ny+dy[j]) continue;
				final int nnx = nx+dx[j];
				final int nny = ny+dy[j];
				int point = itemDist[nny][nnx];
				if(isItem){
					point = point==0?-2:-1;
					isItem = false;
				}
				if(bestItem>point){
					bestItem = point;
					// bestDog = dogDist[ny][nx] // 莉翫�ｯ迥ｬ(縺ｮ霍晞屬)縺ｯ辟｡隕�
					bm1 = i;
					bm2 = j;
					bm3 = -1;
				}
				
			}
		}
		if(bm1==4 || dogCount+stoneCount==4 && dogCount>1){
			// (陦薙ｒ菴ｿ繧上↑縺代ｌ縺ｰ)隧ｰ縺ｿ
			setSkill = "7 "+pid;
		}
		return ds[bm1]+ds[bm2];
	}
	
	void dump(int[] map, int mask){
		for(int i=0; i<map.length; i++){
			System.err.print((map[i]&mask)>0?1:0);
			if((i+1)%W==0) System.err.println();
		}
		System.err.println();
	}
	
	void dump(int[][] dist){
		for(int i=0; i<dist.length; i++){
			for(int j=0; j<dist[i].length; j++){
				System.err.print(dist[i][j]+"\t");
			}
			System.err.println();
		}
		System.err.println();
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
		bfsDog(dogDist, dog, dogs);
		bfsItem(itemDist, item, items);
	}

	int qy[] = new int[H*W], qx[] = new int[H*W];
	void bfsDog(int[][] dist, int[] list, int n){
		for (int i = 0; i < H; ++i)
			Arrays.fill(dist[i], inf);
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
				if (isFloor(ny, nx, map) && dist[ny][nx] == inf) {
					dist[ny][nx] = dist[y][x] + 1;
					qy[qe] = ny;
					qx[qe] = nx;
					++qe;
				}else if(isStone(ny, nx, map) && dist[ny][nx]==inf)
					dist[ny][nx] = dist[y][x] + 1;
			}
		}
	}
	void bfsItem(int[][] dist, int[] list, int n){
		for (int i = 0; i < H; ++i)
			Arrays.fill(dist[i], inf);
		int qi = 0, qe = 0;
		for(int i=0; i<n; i++){
			if(list[i]==-1) continue;
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
				if (isFloor(ny, nx, map) && !isDog(ny, nx, map)
						&& dist[ny][nx] == inf) {
					dist[ny][nx] = dist[y][x] + 1;
					qy[qe] = ny;
					qx[qe] = nx;
					++qe;
				}else if(isStone(ny, nx, map) && dist[ny][nx]==inf)
					dist[ny][nx] = dist[y][x] + 1;
			}
		}
	}
	
	String searchItemSimple(int pid){
		System.err.println("Player: "+pid);
		String res = searchNearItem(itemDist, item, items, 1, pid);
		if(res==null){
			return walkEachSimple(pid);
		}
		removeFromItemDist(target[pid]);
		return res;
	}
	
	BitSet[] qbs = new BitSet[H*W];
	int[][] bfr = new int[H][W];
	String searchNearItem(int[][] dist, int[] list, int n, int dep, int pid){
		final int offset = dep==0?1:0;
		for (int i = 0; i < H; ++i)
			Arrays.fill(dist[i], inf);
		int qi = 0, qe = 0;
		int ddOld = 0;
		for(int i=0; i<n; i++){
			if(list[i]<0) continue;
			final int y = list[i]/W;
			final int x = list[i]%W;
			dist[y][x] = -1;
		}
		{
			final int y = pos[pid]/W;
			final int x = pos[pid]%W;
			qy[0] = y;
			qx[0] = x;
			ddOld = dogDist[y][x];
			if(ddOld>0) ddOld = 1;
			dist[y][x] = offset;
			qbs[0] = mapToBS(map, mss);
			qe++;
		}
		int ay = -1, ax = -1;
		BitSet lastMap = null;
		out: while (qi < qe) {
			int y = qy[qi], x = qx[qi];
			BitSet smap = qbs[qi];
			++qi;
			for (int i = 0; i < 4; ++i) {
				final int ny = y+dy[i];
				final int nx = x+dx[i];
				final int nny = ny+dy[i];
				final int nnx = nx+dx[i];
				// 岩の向こうの犬は無視していることに注意
				if(dist[ny][nx]==inf && !isWall(ny, nx, map)
						// 岩がないか、押せる岩
						&& (!get(ny, nx, smap) || !get(nny, nnx, smap) && !isWall(nny, nnx, map)
								&& ((dist[y][x]+1)!=1 || !isDog(nny, nnx, map) && !isNinja(nny, nnx, map)))
						&& (dist[y][x]+1)/2<dogDist[ny][nx]){
					dist[ny][nx] = dist[y][x]+1;
					bfr[ny][nx] = 3-i;
					qy[qe] = ny;
					qx[qe] = nx;
					BitSet newbs = (BitSet)smap.clone();
					if(get(ny, nx, smap)){
						clear(ny, nx, newbs);
						set(nny, nnx, newbs);
					}
					qbs[qe] = newbs;
					++qe;
				}else if(dist[ny][nx]==-1 && (!get(ny, nx, smap) || !get(nny, nnx, smap) && !isWall(nny, nnx, map))
						&& (dist[y][x]+2)/2<dogDist[ny][nx]){
					ay = ny;
					ax = nx;
					lastMap = smap;
					if(get(ny, nx, smap)){
						clear(ny, nx, lastMap);
						set(nny, nnx, lastMap);
					}
					bfr[ny][nx] = 3-i;
					dist[ny][nx] = dist[y][x]+1;
					target[pid] = xy2idxItem.get(ny*W+nx);
					targetDist[pid] = dist[ny][nx];
					break out;
				}
			}
		}
		if(ay==-1){
			System.err.println("search failed");
			return null;
		}
		int oay = ay;
		int oax = ax;
//		dump(dist);
//		dist[ay][ax] = -3;
		int dst = dist[ay][ax]-offset;
		final int oldDst = dst;
//		dump(bfr);
//		boolean reach = dst<=2;
		int[] dir = {-1, -1};
		while(dist[ay][ax]!=offset){
			dist[ay][ax] = -2;
			final int newy = ay+dy[bfr[ay][ax]];
			final int newx = ax+dx[bfr[ay][ax]];
			if(--dst<2) dir[dst] = 3-bfr[ay][ax];
			ay = newy;
			ax = newx;
//			for(int i=0; i<4; i++){
//				if(dist[ay+dy[i]][ax+dx[i]]==dist[ay][ax]-1){
//					dist[ay][ax] = -2;
//					ay += dy[i];
//					ax += dx[i];
//					break;
//				}
//			}
		}
//		dist[oay][oax] = -3;
		String res = "";
		if(dir[1]!=-1){
			removeNinja(ay, ax, pid, map);
			// apply stone to map
			int ny = ay+dy[dir[0]];
			int nx = ax+dx[dir[0]];
			if(isStone(ny, nx, map)){
				removeStone(ny, nx, map);
				setStone(ny+dy[dir[0]], nx+dx[dir[0]], map);
			}
			if(dep==0){
				setNinja(ny, nx, pid, map);
//				if(oldDst==1) removeFromItemDist(oay, oax);
				res = ds[dir[0]];
			}else{
				ny += dy[dir[1]];
				nx += dx[dir[1]];
				if(isStone(ny, nx, map)){
					removeStone(ny, nx, map);
					setStone(ny+dy[dir[1]], nx+dx[dir[1]], map);
				}
				setNinja(ny, nx, pid, map);
				if(oldDst<=2) removeFromItemDist(oay, oax);
				res = ds[dir[0]]+ds[dir[1]];
			}
		}else{
			removeNinja(ay, ax, pid, map);
			// apply stone to map
			int ny = ay+dy[dir[0]];
			int nx = ax+dx[dir[0]];
			setNinja(ny, nx, pid, map);
			if(isStone(ny, nx, map)){
				removeStone(ny, nx, map);
				setStone(ny+dy[dir[0]], nx+dx[dir[0]], map);
			}
			removeFromItemDist(oay, oax);
			if(dep==0){
				res = ds[dir[0]];
			}else{
//				pos[pid] = ny*W + nx;
				String add = searchNearItem(dist, list, n, dep-1, pid);
				if(add==null) res = ds[dir[0]];
				else res = ds[dir[0]]+add;
			}
		}
		if(nextToDog(pid)) return null;
		return res;
//		for(int i=0; i<4; i++){
//			if(dist[ay+dy[i]][ax+dx[i]]==-2){
//				removeNinja(ay, ax, pid, map);
//				setNinja(ay+dy[i], ax+dx[i], pid, map);
//				if(dep==0) return ds[i];
//				// もう一段探索
//				final int ny = ay+dy[i];
//				final int nx = ax+dx[i];
//				for(int j=0; j<4; j++){
//					if(dist[ny+dy[j]][nx+dx[j]]<=-2){
//						if(dist[ny+dy[j]][nx+dx[j]]==-3){
//							// itemDist再構築
//							removeFromItemDist(ny+dy[i], nx+dx[i]);
//						}
//						removeNinja(ny, nx, pid, map);
//						setNinja(ny+dy[j], nx+dx[j], pid, map);
//						return ds[i]+ds[j];
//					}
//				}
//			}else if(dist[ay+dy[i]][ax+dx[i]]==-3){
//				// itemDistを再構築して再探索
//				removeFromItemDist(ay+dy[i], ax+dx[i]);
//				removeNinja(ay, ax, pid, map);
//				setNinja(ay+dy[i], ax+dx[i], pid, map);
//				if(dep==0) return ds[i];
//				String add = searchNearItem(dist, list, n, dep-1, pid);
//				if(add==null) return ds[i]; // add==null はあるの？
//				return ds[i]+add;
//			}
//		}
//		System.err.println("search failed");
//		return null;
	}
	
//	void applyStoneMap(BitSet newmap, int[] map){
//		for(int i=0; i<map.length; i++){
//			map[i] &= ~mss;
//			if(newmap.get(i)) map[i] |= mss;
//		}
//	}
	
	boolean nextToDog(int pid){
		final int y = pos[pid]/W;
		final int x = pos[pid]%W;
		return dogDist[y][x]==1;
	}
	
	void removeFromItemDist(int idx){
		item[idx] = -1;
		bfsItem(itemDist, item, items);
	}
	void removeFromItemDist(int y, int x){
		int idx = y*W+x;
		for(int i=0; i<items; i++){
			if(item[i]==idx){
				item[i] = -1;
				break;
			}
		}
		bfsItem(itemDist, item, items);
	}
	
	boolean get(int y, int x, BitSet bs){return bs.get(y*W+x);}
	void clear(int y, int x, BitSet bs){bs.clear(y*W+x);}
	void set(int y, int x, BitSet bs){bs.set(y*W+x);}
	
	BitSet mapToBS(int[] map, int mask){
		BitSet bs = new BitSet(map.length);
		for(int i=0; i<map.length; i++) if((map[i]&mask)>0) bs.set(i);
		return bs;
	}
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
