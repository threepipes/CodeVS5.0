import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

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

	int turn = 0;
	final static int H = 17;
	final static int W = 14;
	int[] map = new int[H*W];
	int[] submap = new int[H*W];
	int[] basemap = new int[H*W];
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
	int[] baseitem = new int[20];
	int items;
	int[][] dogDist = new int[H][W];
	HashMap<Integer, Integer> id2idxDog = new HashMap<>();
	int[] dog = new int[H*W];
	BitSet defaultDogMap = new BitSet(H*W);
	int dogs;
	
	int[] pos = new int[2];
	int[] target = new int[2];
	int[] targetDist = new int[2];
	int[] subpos = new int[2];
	int[] basepos = new int[2];
	
	int[][] point = new int[H][W];
	
	void setStone(int y, int x, int[] s){s[y*W+x] |= mss;}
	void setWall(int y, int x, int[] s){s[y*W+x] |= msw;}
	void setNinja1(int y, int x, int[] s){s[y*W+x] |= msn1;}
	void setNinja2(int y, int x, int[] s){s[y*W+x] |= msn2;}
	void setNinja(int y, int x, int id,int[]s, int[] pos){s[y*W+x]|=msn1<<id;pos[id]=y*W+x;}
	void setItem(int y, int x, int[] s){s[y*W+x] |= msi;}
	void setDog(int y,int x,int id,int[] s){s[y*W+x]|=id;}
	
	int getDog(int y, int x, int[] s){return s[y*W+x]&msd;}
	
	boolean isStone(int y, int x, int[] s){return (s[y*W+x]&mss)>0;}
	boolean isWall(int y, int x, int[] s){return (s[y*W+x]&msw)>0;}
	boolean isFloor(int y, int x, int[] s){return (s[y*W+x]&(mss|msw))==0;}
	boolean isStoneMove(int y, int x, int[] s){return (s[y*W+x]&msEmpty)==0;}
	boolean isNinja(int y, int x, int[] s){return (s[y*W+x]&(msn1|msn2))>0;}
	boolean isItem(int y, int x, int[] s){return (s[y*W+x]&msi)>0;}
	boolean isDog(int y, int x, BitSet bs){return bs.get(y*W+x);}
	
	void removeStone(int y, int x, int[] s){s[y*W+x] &= ~mss;}
	void removeNinja(int y, int x, int id, int[] s){s[y*W+x]&=~(msn1<<id);}
	void removeDogFromTable(int y, int x, int[] s){
		if((s[y*W+x]&msd)==0) return;
		id2idxDog.remove(s[y*W+x]&msd);
		s[y*W+x] &= ~msd;
	}
	
	void mapToDogBS(int[] s, BitSet bs){
		for(int i=0;i<s.length;i++)
			if((s[i]&msd)>0) bs.set(i);
	}
	
	void addItem(int y, int x, int i){item[i] = y*W+x;}
	void addDog(int y, int x, int i, int[] list){list[i]=y*W+x;}
	
	String setSkill;
	String think(ContestScanner sc) throws IOException {
		long millitime = sc.nextLong();
		setSkill = null;
		sc.nextInt();
		Arrays.fill(map, 0);
		Arrays.fill(emap, 0);
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
			for (int i = 0; i < n; ++i) {
				int id = sc.nextInt(), row = sc.nextInt(), col = sc.nextInt();
				pos[id] = row*W+col;
				subpos[id] = pos[id];
				basepos[id] = pos[id];
				setNinja(row, col, id, map, pos);
			}
			// zombie
			n = sc.nextInt();
			dogs = n;
			id2idxDog.clear();
			for (int i = 0; i < n; ++i) {
				int id = sc.nextInt(), row = sc.nextInt(), col = sc.nextInt();
				id2idxDog.put(id, i);
				setDog(row, col, id+1, map);
				addDog(row, col, i, dog);
			}
			mapToDogBS(map, defaultDogMap);
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
			for(int i=0; i<items; i++){
				subitem[i] = item[i];
				baseitem[i] = item[i];
			}
			for(int i=0; i<map.length; i++){
				submap[i] = map[i];
				basemap[i] = map[i];
			}
		}
		{
			ePow = sc.nextInt();
			int map_row = sc.nextInt(), map_col = sc.nextInt();
			boolean map[][] = new boolean[map_row][map_col];
			for(int i=0; i<H; i++){
				char[] s = sc.nextToken().toCharArray();
				for(int j=0; j<W; j++){
					if(s[j]=='O') setStone(i, j, emap);
					else if(s[j]=='W') setWall(i, j, emap);
				}
			}
			for (int i = 0, n = sc.nextInt(); i < n; ++i) {
				int id = sc.nextInt(), row = sc.nextInt(), col = sc.nextInt();
				epos[id] = row*W+col;
				setNinja(row, col, id, emap, epos);
			}
			eDogs = sc.nextInt();
			eDogMap.clear();
			for (int i = 0; i < eDogs; ++i) {
				int id = sc.nextInt(), row = sc.nextInt(), col = sc.nextInt();
				setDog(row, col, id+1, emap);
				addDog(row, col, i, eDogList);
			}
			mapToDogBS(emap, eDogMap);
			for (int i = 0, n = sc.nextInt(); i < n; ++i) {
				int row = sc.nextInt(), col = sc.nextInt();
			}
			for (int i = 0; i < skills; ++i) {
				eSkillUse[i] = sc.nextInt();
			}
		}
		System.err.println("turn:"+turn);
		if(turn==37){
			System.err.println("stop");
		}
		turn++;
		bfsDog(eDogDist, eDogList, eDogs, emap);
		return createCommand();
	}
	
	class Command{
		String com = "";
		List<Integer> list = new ArrayList<>();
		int len;
		int point;
		boolean moveStone = false;
		Command(int[] d){
			for(int i=0; i<d.length; i++){
				list.add(d[i]);
				com += ds[d[i]];
			}
			len = d.length;
		}
		Command(int d){com = ds[d];len = 1;list.add(d);}
		Command(int d1, int d2){com = ds[d1]+ds[d2];len = 2;list.add(d1);list.add(d2);}
		void setPoint(int p){
			this.point = p;
		}
		int apply(int y, int x){
			for(int i: list){
				y += dy[i];
				x += dx[i];
			}
			return y*W+x;
		}
		void add(int d){
			if(len>=3){
				System.err.println("Error: Can't append command!");
				return;
			}
			list.add(d);
			com += ds[d];
			len++;
		}
		void add(Command c){
			if(len+c.len>3){
				System.err.println("Error: Can't append command!");
				return;
			}
			list.addAll(c.list);
			com += c.com;
			len += c.len;
			point = c.point;
			moveStone |= c.moveStone;
		}
		@Override
		public String toString() {
			return com;
		}
	}
	
	boolean modeEscape = false;
	// TODO やりかけ
	// 脱出モードに入ったときに、犬配置を無視したアイテム距離で行動すること
	String createCommand(){
		order(dog, false);
		setSkill = useAttackSkill();
		Command[] p = new Command[2];
		for(int i=0; i<2; i++)
			p[i] = searchItemSimple(i, false, false);
		if(checkDoCopy(p)){
			p = doCopyCommand();
//			if(!modeEscape && (p[0]==null || p[1]==null)){
//				modeEscape = true;
//				p = doCopyCommand();
//			}
		}else modeEscape = false;
		if(p[0]==null || p[1]==null){
			resetBase();
			int[] pos = getThunderChoice();
			int bestIdx = -1;
			for(int i=0; i<pos.length; i++){
				final int y = pos[i]/W;
				final int x = pos[i]%W;
				resetBase();
				initThunder(y, x);
				Command[] tmp = new Command[2];
				for(int j=0; j<2; j++) tmp[j] = searchItemSimple(j, true, modeEscape);
				if(tmp[0]==null || tmp[1]==null) continue;
				if(p[0]==null||p[1]==null
						|| Math.min(p[0].point, p[1].point)<Math.min(tmp[0].point, tmp[1].point)){
					p = tmp.clone();
					bestIdx = i;
				}
				if(bestIdx>=0){
					setSkill = SK_THUND_ME+" "+pToStr(pos[bestIdx]);
				}
			}
		}
		String res = "";
		if(p[0]==null || p[1]==null){
			modeEscape = true;
			resetBase();
			order(dog, false);
			for(int i=0; i<2; i++){
				p[i] = searchItemSimple(i, false, true);
				if(p[i] == null) p[i] = walkEachSimple(i, false, true);
				update(i);
			}
			resetBase();
//			if(p[0]==null || p[1]==null)
//				for(int i=0; i<2; i++) res += walkEachSimple(i, false, true)+"\n";
//			else 
			for(int i=0; i<2; i++) res += p[i]+"\n";
		}else for(int i=0; i<2; i++) res += p[i]+"\n";
		if(setSkill != null) res = "3\n" + setSkill + "\n" + res;
		else res = "2\n" + res;
		System.err.println(res);
		return res;
	}
	
	Command[] doCopyCommand(){
		Command[] p = new Command[2];
		// 分身の術
		resetBase();
		int[] choice = getCopyChoice();
		int bestIdx = -1;
		for(int i=0; i<choice.length; i++){
			if(choice[i] == -1) continue;
			resetBase();
			final int y = choice[i]/W;
			final int x = choice[i]%W;
			initCopying(y, x);
			Command[] tmp = new Command[2];
			for(int j=0; j<2; j++) tmp[j] = searchItemSimple(j, true, modeEscape);
			if(tmp[0]==null || tmp[1]==null
					|| tmp[0].moveStone|tmp[1].moveStone
					&& !checkSafe(tmp, choice[i])) continue;
			if(p[0]==null||p[1]==null
					|| Math.min(p[0].point, p[1].point)<Math.min(tmp[0].point, tmp[1].point)){
				p = tmp.clone();
				bestIdx = i;
			}
		}
		if(bestIdx>=0){
			setSkill = SK_COPY_ME+" "+pToStr(choice[bestIdx]);
		}else{
			for(int i=0; i<choice.length; i++){
				if(choice[i] == -1) continue;
				resetBase();
				final int y = choice[i]/W;
				final int x = choice[i]%W;
				initCopying(y, x);
				Command[] tmp = new Command[2];
				for(int j=0; j<2; j++){
					walkEachSimple(j, true, false);
					update(j);
				}
				if(tmp[0]==null || tmp[1]==null
						|| tmp[0].moveStone|tmp[1].moveStone
							&& !checkSafe(tmp, choice[i])) continue;
				if(p[0]==null||p[1]==null
						|| Math.min(p[0].point, p[1].point)<Math.min(tmp[0].point, tmp[1].point)){
					p = tmp.clone();
					bestIdx = i;
				}
			}
		}
		return p;
	}
	
	// 石を動かした場合に敵の動きが変わるので，simulateDogがあてにならない
	// 本当に生き残れるかチェックする
	boolean checkSafe(Command[] com, int copy){
		resetBase();
		int[] p = new int[2];
		for(int i=0; i<2; i++){
			int y = pos[i]/W;
			int x = pos[i]%W;
			for(int d: com[i].list){
				y += dy[d];
				x += dx[d];
				if(isStone(y, x, map)){
					removeStone(y, x, map);
					setStone(y+dy[d], x+dx[d], map);
				}
			}
			p[i] = y*W+x;
		}
		BitSet bs = simulateDogs(new int[]{copy}, map, dogs, dog, id2idxDog);
		for(int i=0; i<2; i++){
//			if(p[i]/W==cy && p[i]%W==cx) return false;
			if(bs.get(p[i])){
				System.err.println("Not safe move");
				return false;
			}
		}
		return true;
	}
	
	boolean checkDoCopy(Command[] p){
		return pow>=cost[SK_COPY_ME]
				&& Math.min(dogDist[basepos[0]/W][basepos[0]%W],dogDist[basepos[1]/W][basepos[1]%W])<=2
				&& (p[0]==null || p[1]==null)
//				(
//						p[0]==null && p[1]==null
//						|| p[0]==null && dogDist[basepos[0]/W][basepos[0]%W]<4
//						|| p[1]==null && dogDist[basepos[1]/W][basepos[1]%W]<4
//				)
				;
	}
	
	String pToStr(int pos){
		return (pos/W)+" "+(pos%W);
	}
	
	// 自陣(tx,ty)に雷を落とした場合の準備
	void initThunder(int cy, int cx){
		// 岩の消去
		removeStone(cy, cx, map);
		// order()
		order(dog, false);
	}
	
	int[] getThunderChoice(){
		HashSet<Integer> res = new HashSet<>();
		for(int i=0; i<2; i++){
			final int y = pos[i]/W;
			final int x = pos[i]%W;
			for(int d=0; d<4; d++){
				res.add((y+dy[d])*W+x+dx[d]);
			}
		}
		int[] pos = new int[res.size()];
		int i=0;
		for(int p: res){
			pos[i++] = p;
		}
		return pos;
	}
	
	int[] choiceCenterY = {2,   2, H-3, H-3, H/2};
	int[] choiceCenterX = {2, W-3,   2, W-3, W/2};
	int[] getCopyChoice(){
//		int best = 0;
//		int pos = 0;
//		for(int i=0; i<H; i++){
//			for(int j=0; j<W; j++){
//				if(sdist[i][j]>best){
//					best = sdist[i][]
//				}
//			}
//		}
		
		int[] res = new int[5];
		for(int i=0; i<5; i++){
			res[i] = -1;
			out:for(int y=choiceCenterY[i]-1; y<=choiceCenterY[i]+1; y++){
				for(int x=choiceCenterX[i]-1; x<=choiceCenterX[i]+1; x++){
					if(!isStone(y, x, map)){
						res[i] = y*W+x;
						break out;
					}
				}
			}
		}
		return res;
	}
	
	// 分身の術を自陣の(cx,cy)に作った場合の準備
	void initCopying(int cy, int cx){
		BitSet dogmap = simulateDogs(new int[]{cy*W+cx}, map, dogs, dog, id2idxDog);
		order(getTmpDogList(dogmap), true);
	}
	
	String useAttackSkill(){
		int stone = stoneAttack();
		if(stone != -1){
			return SK_STONE_EN+" "+(stone/W)+" "+(stone%W);
		}else if(nesc != -1 && eSkillUse[SK_COPY_ME]>4 && ePow>eSkillUse[SK_COPY_ME]){
			return SK_COPY_EN+" "+(epos[nesc]/W)+" "+(epos[nesc]%W);
		}
		return null;
	}
	
	int[] eSkillUse = new int[8];
	int[][] eDogDist = new int[H][W];
	int[] eDogList = new int[H*W];
	BitSet eDogMap = new BitSet(H*W);
	int eDogs, ePow;
	int[] emap = new int[H*W];
	int[] epos = new int[2];
	int nesc;
	int stoneAttack(){
		if(cost[SK_STONE_EN]>pow) return -1;
		nesc = -1;
		for(int pid=0; pid<2; pid++){
			final int y = epos[pid]/W;
			final int x = epos[pid]%W;
			if(!escapable(y, x, emap) && eDogDist[y][x]==1){
				nesc = pid;
				continue;
			}
			int count = 0;
			if(eDogDist[y][x]>2) continue;
			for(int i=0; i<8; i++){
				final int ny = y+dy8[i];
				final int nx = x+dx8[i];
				if(isStone(ny, nx, emap) || isWall(ny, nx, emap) || isDog(ny, nx, eDogMap))
					count++;
			}
			if(count<4) continue;
			for(int sy=y-2; sy<=y+2; sy++){
				for(int sx=x-2; sx<=x+2; sx++){
					if(sy<1||sy>=H-1||sx<1||sx>=W-1||sy==y && sx==x
							|| isStone(sy, sx, emap)
							|| isDog(sy, sx, eDogMap)
							|| isNinja(sy, sx, emap))
						continue;
					setStone(sy, sx, emap);
					if(!escapable(y, x, emap)){
						return sy*W+sx;
					}
					removeStone(sy, sx, emap);
				}
			}
		}
		return -1;
	}
	
	boolean escapable(int y, int x, int[] map){
		for(int i=0; i<4; i++){
			if(!okMove(y, x, i, map) || eDogDist[y+dy[i]][x+dx[i]] == 0) continue;
			final int ny = y+dy[i];
			final int nx = x+dx[i];
			for(int j=0; j<5; j++){
				if(y==ny&&x==nx) continue;
				final int nnx = nx+dx[j];
				final int nny = ny+dy[j];
				if(!okMove(ny, nx, j, map) || eDogDist[nny][nnx] <= 1
						|| nny==y && nnx==x)
					continue;
				return true;
			}
		}
		return false;
	}
	
	void update(int pid){
		// 確定した分を更新
		// 現在updateされてなくてよいもの:
		// 		xy2idx : item[]から消えれば参照されることはない
		//		doglist: bitSetの一時dogMapを用いているため
		for(int i=0; i<map.length; i++) submap[i] = map[i];
		mapToDogBS(map, defaultDogMap);
		subpos[pid] = pos[pid];
		for(int i=0; i<items; i++) subitem[i] = item[i];
	}
	
	void reset(int pid){
		// マップ、プレイヤー座標、アイテム を戻す(自分のターンが始まる前=2Pの場合、1Pの動作後の状態)
		// 今後、スキルなどもありうる
		for(int i=0; i<map.length; i++) map[i] = submap[i];
		mapToDogBS(map, defaultDogMap);
		pos[pid] = subpos[pid];
		for(int i=0; i<items; i++) item[i] = subitem[i];
	}
	
	void resetBase(){
		// ターン初期状態に戻す
		for(int i=0; i<map.length; i++) map[i] = basemap[i];
		mapToDogBS(map, defaultDogMap);
		for(int i=0; i<2; i++) pos[i] = basepos[i];
		for(int i=0; i<items; i++) item[i] = baseitem[i];
	}
	
	Command walkEachSimple(int pid, boolean copy, boolean last){
		int bestItem = inf;
		int bestDog = 0;
		int bm1 = 4, bm2 = 4, bm3 = -1;
		boolean isItem = false;
		final int y = pos[pid]/W;
		final int x = pos[pid]%W;
		int dogCount = 0;
		int stoneCount = 0;
		for(int i=0; i<4; i++){
			final int ny = y+dy[i];
			final int nx = x+dx[i];
			if(isDog(ny, nx, defaultDogMap)) dogCount++;
			if(!okMove(y, x, i, map) || dogDist[y+dy[i]][x+dx[i]] == 0) continue;
			else if(isStone(ny, nx, map)) stoneCount++;
			if(isItem(ny, nx, map)) isItem = true;
			for(int j=0; j<5; j++){
				if(!okMove(ny, nx, j, map)
						|| dogDist[ny+dy[j]][nx+dx[j]] <= 1
						|| (copy&&dogDist[ny+dy[j]][nx+dx[j]]==0))
					continue;
				final int nnx = nx+dx[j];
				final int nny = ny+dy[j];
				int point = itemDist[nny][nnx];
				if(isItem){
					point = point==0?-2:-1;
					isItem = false;
				}
				if(bestItem>point || bestItem==point&&dogDist[nny][nnx]>bestDog){
					bestItem = point;
					bestDog = dogDist[nny][nnx];
					bm1 = i;
					bm2 = j;
					bm3 = -1;
				}
				
			}
		}
		if(last && cost[SK_ATTACK]<=10 && cost[SK_ATTACK]<=pow && dogCount>4){
			setSkill = "7 "+pid;
			for(int i=0; i<8; i++){
				removeDogFromTable(y+dy8[i], x+dx8[i], map);
			}
			bfsDog(dogDist, dog, dogs, map);
			Command res = searchNearItem(itemDist, item, items, 1, pid, false, true);
			if(res != null) return res;
		}else if(last && cost[SK_COPY_ME]<=pow){
			int max = 0;
			int my = 0;
			int mx = 0;
			bfsPos(sdist, basepos, 2);
			for(int i=0; i<H; i++){
				for(int j=0; j<W; j++){
					if(sdist[i][j]!=inf && sdist[i][j]>max){
						max = sdist[i][j];
						my = i;
						mx = j;
					}
				}
			}
			setSkill = SK_COPY_ME+" "+my+" "+mx;
		}
		return new Command(bm1,bm2);
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
	
	boolean okMove(int y, int x, int d, int[] map){
		if(d==4) return true;
		final int ny = y+dy[d];
		final int nx = x+dx[d];
		if(isFloor(ny, nx, map)) return true;
		if(!isStone(ny, nx, map)) return false;
		return isStoneMove(ny+dy[d], nx+dx[d], map);
	}
	
	int[] tmpDogList = new int[H*W];
	int[] getTmpDogList(BitSet bs){
		for(int i=bs.nextSetBit(0),idx=0;i!=-1;i=bs.nextSetBit(i+1),idx++)
			tmpDogList[idx] = i;
		return tmpDogList;
	}
	
	void order(int[] dog, boolean ignoreDog) {
		bfsDog(dogDist, dog, dogs, map);
		bfsItem(itemDist, item, items, map, defaultDogMap, ignoreDog);
	}

	int qy[] = new int[H*W], qx[] = new int[H*W];
	void bfsDog(int[][] dist, int[] list, int n, int[] map){
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
	void bfsItem(int[][] dist, int[] list, int n, int[] map, BitSet dogmap, boolean ignoreDog){
		for (int i = 0; i < H; ++i)
			Arrays.fill(dist[i], inf);
		int qi = 0, qe = 0;
		for(int i=0; i<n; i++){
			if(list[i]==-1) continue;
			final int y = list[i]/W;
			final int x = list[i]%W;
			if(isStone(y, x, map)) continue;
			qy[qe] = y;
			qx[qe] = x;
			dist[y][x] = 0;
			qe++;
		}
		while (qi < qe) {
			int y = qy[qi], x = qx[qi];
			if(dist[y][x]>=inf){
				System.err.println("INF");
			}
			++qi;
			for (int i = 0; i < 4; ++i) {
				int ny = y+dy[i];
				int nx = x+dx[i];
				if (isFloor(ny, nx, map) && (!isDog(ny, nx, dogmap) || ignoreDog)
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
	
	void bfsPos(int[][] dist, int[] list, int n){
		for(int i=0; i<H; i++)
			Arrays.fill(dist[i], inf);
		int qi = 0, qe = 0;
		for(int i=0; i<n; i++){
			final int y = list[i]/W;
			final int x = list[i]%W;
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
				}
			}
		}
	}
	
	int[][] sdist = new int[H][W];
	Queue<Integer> qu = new PriorityQueue<>();
	BitSet dogMap = new BitSet(H*W);
	BitSet simulateDogs(int[] pos, int[] map, int dogs
			, int[] dogList, HashMap<Integer, Integer> id2idx){
		bfsPos(sdist, pos, pos.length);
		dogMap.clear();
		qu.clear();
		for(int i=0; i<dogs; i++){
			final int y = dogList[i]/W;
			final int x = dogList[i]%W;
			final int id = getDog(y, x, map)-1;
			qu.add((sdist[y][x]<<10)|id);
			dogMap.set(dogList[i]);
		}
		while(!qu.isEmpty()){
			if(id2idx.get(qu.peek()&msd)==null){
				System.err.println("Error: id2idx null");
			}
			final int idx = id2idx.get(qu.poll()&msd);
			final int y = dogList[idx]/W;
			final int x = dogList[idx]%W;
			for(int i=0; i<4; i++){
				final int ny = y+dy[i];
				final int nx = x+dx[i];
				if(!isFloor(ny, nx, map) 
						|| dogMap.get(ny*W+nx)
						|| sdist[ny][nx]>=sdist[y][x])
					continue;
				dogMap.clear(y*W+x);
				dogMap.set(ny*W+nx);
				break;
			}
		}
		return dogMap;
	}
	
	Command searchItemSimple(int pid, boolean copy, boolean esc){
		System.err.println("Player: "+pid);
		Command res = searchNearItem(itemDist, item, items, 1, pid, copy, esc);
//		if(res==null){
//			reset(pid);
//			return walkEachSimple(pid);
//		}
		update(pid);
		removeFromItemDist(target[pid], copy);
		return res;
	}
	
	final static int[] dy8 = {1, 1, 1, 0, 0,-1,-1,-1};
	final static int[] dx8 = {1, 0,-1, 1,-1, 1, 0,-1};
	void culcEval(){
		for(int i=0; i<H; i++)
			Arrays.fill(point[i], 0);
		for(int i=1; i<H-1; i++){
			for(int j=1; j<W-1; j++){
				if(dogDist[i][j]==0 || isStone(i, j, map)) continue;
				point[i][j] = isItem(i, j, map)?150:100;
				for(int k=0; k<8; k++){
					final int y = i+dy8[k];
					final int x = j+dx8[k];
					if(dogDist[y][x]==0){
						point[i][j] -= 10;
					}else if(isStone(y, x, map)){
						point[i][j] -= 3;
					}else if(isWall(y, x, map)){
						point[i][j] -= 10;
					}
					if(isItem(y, x, map)){
						point[i][j] += 5;
					}
				}
			}
		}
	}
	
	BitSet[] qbs = new BitSet[H*W];
	int[][] bfr = new int[H][W];
	Command searchNearItem(int[][] dist, int[] list, int n, int dep, int pid, boolean copy, boolean esc){
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
		final int py = pos[pid]/W;
		final int px = pos[pid]%W;
		{
			final int y = py;
			final int x = px;
			qy[0] = y;
			qx[0] = x;
			ddOld = dogDist[y][x];
			if(ddOld>0) ddOld = 1;
			dist[y][x] = offset;
			qbs[0] = mapToBS(map, mss);
			qe++;
		}
		culcEval();
		int ay = -1, ax = -1;
//		BitSet lastMap = null;
		int best = esc||dogs<20?0:100;
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
								&& ((dist[y][x]+1)>2 || dogDist[nny][nnx]!=0 && !isNinja(nny, nnx, map)))
						&& (((dist[y][x]+1)/2<dogDist[ny][nx]) || copy&&esc&&dogDist[ny][nx]>0)){
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
					if(point[ny][nx]+dist[ny][nx]>best && (Math.abs(ny-py)+Math.abs(nx-px)>=5)){
						best = point[ny][nx]+dist[ny][nx]+best;
						ay = ny;
						ax = nx;
//						lastMap = newbs;
					}
					++qe;
				}else if(dist[ny][nx]==-1 && (!get(ny, nx, smap) || !get(nny, nnx, smap) && !isWall(nny, nnx, map))
						&& ((dist[y][x]+2)/2<dogDist[ny][nx]  || copy&&esc&&dogDist[ny][nx]>0)){
					ay = ny;
					ax = nx;
//					lastMap = smap;
//					if(get(ny, nx, smap)){
//						clear(ny, nx, lastMap);
//						set(nny, nnx, lastMap);
//					}
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
		}
//		String res = "";
		Command res = null;
		boolean moveStone = false;
		if(dir[1]!=-1){
			removeNinja(ay, ax, pid, map);
			// apply stone to map
			int ny = ay+dy[dir[0]];
			int nx = ax+dx[dir[0]];
			if(isStone(ny, nx, map)){
				removeStone(ny, nx, map);
				setStone(ny+dy[dir[0]], nx+dx[dir[0]], map);
				moveStone = true;
			}
			if(dep==0){
				setNinja(ny, nx, pid, map, pos);
//				res = ds[dir[0]];
				res = new Command(dir[0]);
				res.setPoint(dogDist[ny][nx]);
			}else{
				ny += dy[dir[1]];
				nx += dx[dir[1]];
				if(isStone(ny, nx, map)){
					removeStone(ny, nx, map);
					setStone(ny+dy[dir[1]], nx+dx[dir[1]], map);
					moveStone = true;
				}
				setNinja(ny, nx, pid, map, pos);
				if(oldDst<=2) removeFromItemDist(oay, oax, copy);
//				res = ds[dir[0]]+ds[dir[1]];
				res = new Command(dir);
				res.setPoint(dogDist[ny][nx]);
			}
		}else{
			removeNinja(ay, ax, pid, map);
			// apply stone to map
			int ny = ay+dy[dir[0]];
			int nx = ax+dx[dir[0]];
			setNinja(ny, nx, pid, map, pos);
			if(isStone(ny, nx, map)){
				removeStone(ny, nx, map);
				setStone(ny+dy[dir[0]], nx+dx[dir[0]], map);
				moveStone = true;
			}
			removeFromItemDist(oay, oax, copy);
			res = new Command(dir[0]);
			res.setPoint(dogDist[ny][nx]);
			if(dep>0){
//				pos[pid] = ny*W + nx;
				Command add = searchNearItem(dist, list, n, dep-1, pid, copy, esc);
				if(add!=null) res.add(add);
			}
		}
		if(!copy && nextToDog(res.apply(py, px))) return null;
		res.moveStone |= moveStone;
		return res;
	}
	
	boolean nextToDog(int pos){
		return dogDist[pos/W][pos%W]==1;
	}
	
	void dump(BitSet dog){
		for(int i=0; i<H; i++){
			for(int j=0; j<W; j++){
				System.err.print(dog.get(i*W+j)?1:0);
			}
			System.err.println();
		}
		System.err.println();
	}
	
	void removeFromItemDist(int idx, boolean copy){
		item[idx] = -1;
		bfsItem(itemDist, item, items, map, defaultDogMap, copy);
	}
	void removeFromItemDist(int y, int x, boolean copy){
		int idx = y*W+x;
		for(int i=0; i<items; i++){
			if(item[i]==idx){
				item[i] = -1;
				break;
			}
		}
		bfsItem(itemDist, item, items, map, defaultDogMap, copy);
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
