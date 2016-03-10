import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

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

	int turn = -1;
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
	
	final static int SK_RUN = 0;
	final static int SK_STONE_ME = 1;
	final static int SK_STONE_EN = 2;
	final static int SK_THUND_ME = 3;
	final static int SK_THUND_EN = 4;
	final static int SK_COPY_ME = 5;
	final static int SK_COPY_EN = 6;
	final static int SK_ATTACK = 7;
	
	int[] useSkill = new int[8];
	HashMap<Integer, Integer> xy2idxItem = new HashMap<>();
	int[][] itemDist = new int[H][W];
	int[] item = new int[20];
	int[] subitem = new int[20];
	int[] baseitem = new int[20];
	int items;
	int[][] dogDist = new int[H][W];
	int[][] oldDogDist = new int[H][W];
	HashMap<Integer, Integer> id2idxDog = new HashMap<>();
	int[] dog = new int[H*W];
	BitSet defaultDogMap = new BitSet(H*W);
	int dogs;
	
	int[] pos = new int[2];
	int[] target = new int[2];
	int[] targetDist = new int[2];
	int[] targetPos = {-1, -1};
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
	boolean isDogInMap(int y, int x, int[]s){return (s[y*W+x]&msd)>0;}
	boolean isStoneMove(int y, int x, int[] s, int[][] dogDist){
		return !isStone(y, x, s) && dogDist[y][x]!=0 && !isWall(y, x, s) && !isNinja(y, x, s);
	}
	
	void removeStone(int y, int x, int[] s){s[y*W+x] &= ~mss;}
	void removeNinja(int y, int x, int id, int[] s){s[y*W+x]&=~(msn1<<id);}
	void removeDogFromTable(int y, int x, int[] s){
		if((s[y*W+x]&msd)==0) return;
		id2idxDog.remove(s[y*W+x]&msd);
		s[y*W+x] &= ~msd;
	}
	
	void mapToDogBS(int[] s, BitSet bs){
		bs.clear();
		for(int i=0;i<s.length;i++)
			if((s[i]&msd)>0) bs.set(i);
	}
	
	void addItem(int y, int x, int i){item[i] = y*W+x;}
	void addDog(int y, int x, int i, int[] list){list[i]=y*W+x;}
	
	String setSkill;
	String think(ContestScanner sc) throws IOException {
		turn++;
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
//			int useSkill[] = new int[skills];
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
			oldEPow = ePow;
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
			boolean used = false;
			boolean[] usedSkill = new boolean[skills];
			for (int i = 0; i < skills; ++i) {
				int use = sc.nextInt();
				if(use>eSkillUse[i]){
					usedSkill[i] = true;
					used = true;
				}
				eSkillUse[i] = use;
			}
			if(!used){
				// 使用しなかった
				if(useVirtualStone==turn-1 && oldEPow>=cost[SK_STONE_EN]){
					virtualStone = false;
					System.err.println("Set disable to use virtual stone. turn: "+turn);
				}
			}else{
				if(useVirtualStone==turn-1 && usedSkill[SK_STONE_EN] && isStone(vStone/W, vStone%W, this.map)){
					// 座標判断は簡易(石を動かして現状の場合もある)
					virtualThunder = true;
				}
			}
			if(eSkillUse[SK_STONE_EN]>0){
				virtualStone = true;
			}
		}
		System.err.println("turn:"+turn);
		if(turn==40){
			System.err.println("stop");
		}
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
		Command(){len = 0;}
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
	class CommandSet{
		Command[] com;
		String skill;
		int point;
		CommandSet(Command[] com, String skill, int skillCost){
			this.com = com;
			this.skill = skill;
			for(int i=0; i<2; i++){
				if(com[i] != null) point += com[i].point;
				else{
					point = -inf;
					break;
				}
			}
			point -= sq(skillCost)*6;
		}
		@Override
		public String toString() {
			return (skill==null?"2\n":"3\n"+skill)+com[0]+"\n"+com[1]+"\n";
		}
	}
	
	// 脱出モードに入ったときに、犬配置を無視したアイテム距離で行動すること
//	boolean modeEscape = false;
	boolean[] modeEscape = {false, false};
	// 仮想石置きをするかどうか(攻撃されなかったらfalse)
	boolean virtualStone = true;
	boolean virtualThunder = false; // 仮想石置きに対して雷撃するか(石置き予想が一致したときオンにする)
	int useVirtualStone = -1; // 仮想石置き使用ターン
	// 単純攻撃をするかどうか(回避されたらfalse)
	boolean fastStone = true;
	boolean fastCopy = true;
	
	// アイテムに近い順に行動
	int[] playOrder = {0, 1};
	
	int dist(int p1, int p2){
		return Math.abs(p1/W-p2/W)+Math.abs(p1%W-p2%W);
	}
	
	String createCommand(){
		order(dog, false);
		for(int i=0; i<H; i++) for(int j=0; j<W; j++) oldDogDist[i][j] = dogDist[i][j];
		for(int i=0; i<2; i++) playOrder[i] = i;
		if(itemDist[pos[1]/W][pos[1]%W]<itemDist[pos[0]/W][pos[0]%W] && dist(pos[0],pos[1])>5){
			playOrder[0] = 1;
			playOrder[1] = 0;
		}
		setSkill = useFastSkill();
		Command[] p = new Command[2];
		for(int i=0; i<2; i++)
			p[playOrder[i]] = searchItemSimple(playOrder[i], false, modeEscape[playOrder[i]]);
		CommandSet com = new CommandSet(p, setSkill, setSkill==null?0:1);
//		if(com.point==0){
			if(pow>=cost[SK_RUN] /*&& cost[SK_RUN]<cost[SK_COPY_ME]*/){
				//			p = doRunCommand();
				CommandSet comRun = doRunCommand();
				if(comRun!=null&&comRun.point>com.point) com = comRun;
			}
			if(pow>=cost[SK_THUND_ME]){
				CommandSet comTh = doThunderCommand();
				if(comTh!=null&&comTh.point>com.point) com = comTh;
			}
//		}
		if(com.point==-inf || cost[SK_RUN]>2){
			if(pow>=cost[SK_COPY_ME]
					&& rand.nextInt(1001)<=1000/(eSkillUse[SK_COPY_EN]+1)
					&& (vStone==-1||!virtualStone)){
				//			p = doCopyCommand();
				CommandSet comCp = doCopyCommand();
				if(comCp!=null&&comCp.point>com.point){
					com = comCp;
				}
			}//else modeEscape = false;
		}
		p[0] = com.com[0];
		p[1] = com.com[1];
		setSkill = com.skill;
		String res = "";
		if(p[0]==null || p[1]==null){
//			modeEscape = true;
			for(int i=0; i<2; i++) if(p[i] == null) modeEscape[i] = true;
			resetBase();
			order(dog, false);
			for(int i=0; i<2; i++){
				int player = playOrder[i];
				p[player] = searchItemSimple(player, false, true);
				if(p[player] == null){
					reset(player);
					if(vStone!=-1) removeStone(vStone/W, vStone%W, map);
					p[player] = walkEachSimple(player, false, true);
				}
				update(player);
			}
			resetBase();
//			if(p[0]==null || p[1]==null)
//				for(int i=0; i<2; i++) res += walkEachSimple(i, false, true)+"\n";
//			else 
		}
		for(int i=0; i<2; i++) modeEscape[i] &= !checkGetItem(p[i], i);
		for(int i=0; i<2; i++) res += p[i]+"\n";
		if(setSkill != null) res = "3\n" + setSkill + "\n" + res;
		else res = "2\n" + res;
		System.err.println(res);
		return res;
	}
	
	CommandSet doThunderCommand(){
		Command[] p = new Command[2];
		resetBase();
		int[] pos = getThunderChoice();
		int bestIdx = -1;
		for(int i=0; i<pos.length; i++){
			final int y = pos[i]/W;
			final int x = pos[i]%W;
			resetBase();
			initThunder(y, x);
			Command[] tmp = new Command[2];
			for(int j=0; j<2; j++) tmp[playOrder[j]]
					= searchItemSimple(playOrder[j], false, modeEscape[playOrder[j]]);
			if(tmp[0]==null || tmp[1]==null) continue;
			if(p[0]==null||p[1]==null
					|| p[0].point+p[1].point<tmp[0].point+tmp[1].point){
				p = tmp.clone();
				bestIdx = i;
			}
//			if(bestIdx>=0){
//				setSkill = ;
//			}
		}
		if(bestIdx<0) return null;
		return new CommandSet(p, SK_THUND_ME+" "+pToStr(pos[bestIdx]), cost[SK_THUND_ME]);
	}
	
	boolean checkGetItem(Command com, int pid){
		resetBase();
		int y = pos[pid]/W;
		int x = pos[pid]%W;
		for(int d: com.list){
			y += dy[d];
			x += dx[d];
			if(isItem(y, x, map)){
				targetPos[pid] = -1;
				return true;
			}
		}
		return false;
	}

	boolean hasNull(Command[] t){
		for(int i=0; i<t.length; i++) if(t[i] == null) return true;
		return false;
	}
	
	CommandSet doRunCommand(){
		Command[] p = new Command[2];
		resetBase();
		for(int i=0; i<2; i++){
			int pid = playOrder[i];
			p[pid] = searchNearItem(itemDist, item, items, 2, pid, false, false);
			if(p[pid] == null){
				reset(pid);
				p[pid] = searchNearItem(itemDist, item, items, 2, pid, false, true);
			}
			update(pid);
		}
		if(p[0]!=null && p[1]!=null) return new CommandSet(p, String.valueOf(SK_RUN), cost[SK_RUN]);
		return null;
	}
	
	CommandSet doCopyCommand(){
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
			for(int j=0; j<2; j++){
				int pid = playOrder[j];
				tmp[pid] = searchItemSimple(pid, true, modeEscape[pid]);
				if(tmp[pid]==null){
					reset(pid);
					tmp[pid] = searchItemSimple(pid, true, !modeEscape[pid]);
					if(tmp[pid] != null) modeEscape[pid] = true;
				}
			}
			if(tmp[0]==null || tmp[1]==null
					|| tmp[0].moveStone|tmp[1].moveStone
					&& !checkSafe(tmp, choice[i])) continue;
			if(p[0]==null||p[1]==null
					|| p[0].point+p[1].point<tmp[0].point+tmp[1].point){
				p = tmp.clone();
				bestIdx = i;
			}
		}
		if(bestIdx>=0){
			return new CommandSet(p, SK_COPY_ME+" "+pToStr(choice[bestIdx]), cost[SK_COPY_ME]);
//			setSkill = SK_COPY_ME+" "+pToStr(choice[bestIdx]);
		}else{
			for(int i=0; i<choice.length; i++){
				if(choice[i] == -1) continue;
				resetBase();
				final int y = choice[i]/W;
				final int x = choice[i]%W;
				initCopying(y, x);
				Command[] tmp = new Command[2];
				for(int j=0; j<2; j++){
					walkEachSimple(playOrder[j], true, false);
					update(playOrder[j]);
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
			if(p[0]==null || p[1]==null) return null;
			return new CommandSet(p, SK_COPY_ME+" "+pToStr(choice[bestIdx]), cost[SK_COPY_ME]);
		}
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
		if(isStone(copy/W, copy%W, map)) return false;
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
				final int ny = y+dy[d];
				final int nx = x+dx[d];
				if(isStone(ny, nx, map) && (!virtualStone || virtualThunder || ny*W+x != vStone))
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
		TreeSet<Integer> cand = new TreeSet<>();
		culcListEval(item, items, itemEval);
		for(int i=0; i<H; i++){
			for(int j=0; j<W; j++){
				if(isWall(i, j, map) || isStone(i, j, map) || itemEval[i][j]<0) continue;
				cand.add(itemEval[i][j]<<10|(i*W)+j);
			}
		}
		HashSet<Integer> set = new HashSet<>();
//		int[] res = new int[Math.min(5, cand.size())];
		for(int i=0; i<5&&!cand.isEmpty(); i++){
			set.add(cand.pollFirst()&((1<<10)-1));
		}
		for(int i=0; i<2; i++) set.add(pos[i]);
		int[] res = new int[set.size()];
		int i = 0;
		for(int p: set) res[i++] = p;
		
		
//		culcListEval(dog, dogs, dogEval);
//		for(int y=0; y<H; y++){
//			for(int x=0; x<W; x++){
//				System.err.print((itemEval[y][x]-dogEval[y][x])+"\t");
//			}System.err.println();
//		}System.err.println();
//		int best = 0;
//		int pos = 0;
//		for(int i=0; i<H; i++){
//			for(int j=0; j<W; j++){
//				if(sdist[i][j]>best){
//					best = sdist[i][]
//				}
//			}
//		}
		return res;
	}
	
	// 分身の術を自陣の(cx,cy)に作った場合の準備
	void initCopying(int cy, int cx){
		BitSet dogmap = simulateDogs(new int[]{cy*W+cx}, map, dogs, dog, id2idxDog);
		order(getTmpDogList(dogmap), true);
	}
	
	int vStone;
	int fastSkillCost;
	String useFastSkill(){
		fastSkillCost = 0;
		vStone = stoneAttack(pos, dogDist, map, defaultDogMap, ePow);
		if(vStone != -1 && virtualStone){
			useVirtualStone = turn;
			basemap[vStone] |= mss;
			map[vStone] = submap[vStone] = basemap[vStone];
			System.err.println("put virtual stone");
			return null;
			// このターン攻撃は無し
		}
		
		int stone = stoneAttack(epos, eDogDist, emap, eDogMap, pow);
		if(stone != -1 && rand.nextInt(1000)<=1000/sq(useSkill[SK_STONE_EN]+1)){
			fastSkillCost = cost[SK_STONE_EN];
			return SK_STONE_EN+" "+(stone/W)+" "+(stone%W);
		}else if(nesc != -1 && eSkillUse[SK_COPY_ME]>4 && ePow>eSkillUse[SK_COPY_ME]){
			fastSkillCost = cost[SK_COPY_EN];
			return SK_COPY_EN+" "+(epos[nesc]/W)+" "+(epos[nesc]%W);
		}
		return null;
	}
	
	void removeVirtualStone(){
		if(vStone==-1) return;
		removeStone(vStone/W, vStone%W, map);
	}
	void setVirtualStone(){
		if(vStone==-1) return;
		setStone(vStone/W, vStone%W, map);
	}
	
	int[] eSkillUse = new int[8];
	int[][] eDogDist = new int[H][W];
	int[] eDogList = new int[H*W];
	BitSet eDogMap = new BitSet(H*W);
	int eDogs, ePow, oldEPow;
	int[] emap = new int[H*W];
	int[] epos = new int[2];
	int nesc; // 逃げられない方の忍者
	int stoneAttack(int[] pos, int[][] dogDist, int[] map, BitSet dogMap, int pow){
		nesc = -1;
		if(cost[SK_STONE_EN]>pow) return -1;
		for(int pid=0; pid<2; pid++){
			final int y = pos[pid]/W;
			final int x = pos[pid]%W;
			if(!escapable(y, x, map, dogDist) && dogDist[y][x]==1){
				nesc = pid;
				continue;
			}
			int count = 0;
			if(dogDist[y][x]>2) continue;
			for(int i=0; i<8; i++){
				final int ny = y+dy8[i];
				final int nx = x+dx8[i];
				if(isStone(ny, nx, map) || isWall(ny, nx, map) || isDog(ny, nx, dogMap))
					count++;
			}
			if(count<1) continue;
			count = 0;
			int edir = -1;
			for(int i=0; i<4; i++){
				if(isStone(y+dy[i], x+dx[i], map) || isWall(y+dy[i], x+dx[i], map)) count++;
				else edir = i;
			}
			for(int sy=y-2; sy<=y+2; sy++){
				for(int sx=x-2; sx<=x+2; sx++){
					if(sy<1||sy>=H-1||sx<1||sx>=W-1||sy==y && sx==x
							|| isStone(sy, sx, map)
							|| isDog(sy, sx, dogMap)
							|| isNinja(sy, sx, map)
							|| count==3&&sy==y+dy[edir]&&sx==x+dx[edir])
						continue;
					setStone(sy, sx, map);
					if(!escapable(y, x, map, dogDist)){
						return sy*W+sx;
					}
					removeStone(sy, sx, map);
				}
			}
		}
		return -1;
	}
	
	boolean escapable(int y, int x, int[] map, int[][] dogDist){
		for(int i=0; i<4; i++){
			if(!okMove(y, x, i, map) || dogDist[y+dy[i]][x+dx[i]] == 0) continue;
			final int ny = y+dy[i];
			final int nx = x+dx[i];
			boolean moveStone = false;
			if(isStone(ny, nx, map)){
				removeStone(ny, nx, map);
				setStone(ny+dy[i], nx+dx[i], map);
				moveStone = true;
			}
			boolean ok = false;
			for(int j=0; j<5; j++){
				if(y==ny&&x==nx) continue;
				final int nnx = nx+dx[j];
				final int nny = ny+dy[j];
				if(!okMove(ny, nx, j, map) || dogDist[nny][nnx] <= 1
						|| nny==y && nnx==x)
					continue;
				ok = true;
				break;
			}
			if(moveStone){
				removeStone(ny+dy[i], nx+dx[i], map);
				setStone(ny, nx, map);
			}
			if(ok) return true;
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
		for(int i=0; i<map.length; i++) submap[i] = map[i] = basemap[i];
		mapToDogBS(map, defaultDogMap);
		for(int i=0; i<2; i++) subpos[i] = pos[i] = basepos[i];
		for(int i=0; i<items; i++) subitem[i] = item[i] = baseitem[i];
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
			boolean moveStone = false;
			if(!okMove(y, x, i, map) || !copy&&dogDist[ny][nx] == 0) continue;
			else if(isStone(ny, nx, map)){
				stoneCount++;
				moveStone = true;
				removeStone(ny, nx, map);
				setStone(ny+dy[i], nx+dx[i], map);
			}
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
				if((bestItem>point || bestItem==point&&dogDist[nny][nnx]>bestDog)){
					bestItem = point;
					bestDog = dogDist[nny][nnx];
					bm1 = i;
					bm2 = j;
					bm3 = -1;
				}
			}
			if(moveStone){
				removeStone(ny+dy[i], nx+dx[i], map);
				setStone(ny, nx, map);
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
			return walkEachSimple(pid, true, false);
		}
		return new Command(bm1,bm2);
	}
	
	int dist(int y, int x, int ty, int tx){
		return Math.abs(y-ty)+Math.abs(x-tx);
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
	
	boolean okMove(int y, int x, int d, int[] map, int[][] dogDist){
		if(d==4) return true;
		final int ny = y+dy[d];
		final int nx = x+dx[d];
		if(isFloor(ny, nx, map)) return true;
		if(!isStone(ny, nx, map)) return false;
		return isStoneMove(ny+dy[d], nx+dx[d], map, dogDist);
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
	
	void bfsItem(int[] dist, BitSet map, BitSet dogMap){
//		Arrays.fill(dist, inf);
		int qi = 0, qe = 0;
		for(int i=0; i<H*W; i++){
			if(dist[i]!=0){
				dist[i] = inf;
				continue;
			}
			final int y = i/W;
			final int x = i%W;
			if(map.get(i)){
				continue;
			}
			qy[qe] = y;
			qx[qe] = x;
			qe++;
		}
		while (qi < qe) {
			int y = qy[qi], x = qx[qi];
			++qi;
			for (int i = 0; i < 4; ++i) {
				int ny = y+dy[i];
				int nx = x+dx[i];
				if (!isWall(ny, nx, basemap) && !map.get(ny*W+nx)// && !dogMap.get(ny*W+nx)
						&& dist[ny*W+nx] == inf) {
					dist[ny*W+nx] = dist[y*W+x] + 1;
					qy[qe] = ny;
					qx[qe] = nx;
					++qe;
				}else if(map.get(ny*W+nx) && dist[ny*W+nx]==inf)
					dist[ny*W+nx] = dist[y*W+x] + 1;
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
	
	int getFarPos(int[][] dist, int[] list, int n, BitSet map){
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
		int farPos = 0;
		int farDist = -1;
		while (qi < qe) {
			int y = qy[qi], x = qx[qi];
			++qi;
			for (int i = 0; i < 4; ++i) {
				int ny = y+dy[i];
				int nx = x+dx[i];
				if (!isWall(ny, nx, basemap) && !map.get(ny*W+nx) && dist[ny][nx] == inf) {
					dist[ny][nx] = dist[y][x] + 1;
					qy[qe] = ny;
					qx[qe] = nx;
					if(farDist<dist[ny][nx]){
						farDist = dist[ny][nx];
						farPos = ny*W+nx;
					}
					++qe;
				}
			}
		}
		return farPos;
	}
	
	int[][] sdist = new int[H][W];
	Queue<Integer> qu = new PriorityQueue<>();
	BitSet dogMap = new BitSet(H*W);
	BitSet simulateDogs(int[] pos, int[] map, int dogs
			, int[] dogList, HashMap<Integer, Integer> id2idx){
		if(eSkillUse[SK_STONE_EN]==0) removeVirtualStone();
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
		if(eSkillUse[SK_STONE_EN]==0) setVirtualStone();
		return dogMap;
	}
	
	// dogMapはdogListと同期がとれてる前提
	// dogMapとdogListを更新する
	// bfsPosをあらかじめ呼び出しておくこと
	void simulateDogs(BitSet map, int[] dogList, BitSet dogMap){
//		bfsPos(sdist, pos, pos.length);
		qu.clear();
		for(int i=0; i<dogList.length; i++){
			final int y = dogList[i]/W;
			final int x = dogList[i]%W;
			qu.add((sdist[y][x]<<10)|i);
		}
		while(!qu.isEmpty()){
			final int idx = qu.poll()&msd;
			final int y = dogList[idx]/W;
			final int x = dogList[idx]%W;
			for(int i=0; i<4; i++){
				final int ny = y+dy[i];
				final int nx = x+dx[i];
				final int np = ny*W+nx;
				if(isWall(ny, nx, basemap)
						|| map.get(np)
						|| dogMap.get(np)
						|| sdist[ny][nx]>=sdist[y][x])
					continue;
				dogMap.clear(y*W+x);
				dogMap.set(np);
				dogList[idx] = np;
				break;
			}
		}
	}
	
	Command searchItemSimple(int pid, boolean copy, boolean esc){
		System.err.println("Player: "+pid);
		Command res = searchNearItem(itemDist, item, items, 1, pid, copy, esc);
//		if(res==null){
//			reset(pid);
//			return walkEachSimple(pid);
//		}
		if(res != null){
			// たぶん不要
			update(pid);
			removeFromItemDist(target[pid], copy);
		}
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
	Random rand = new Random(0);
	class Status{
		int y, x;
		BitSet map;
		int dog;
		boolean bfDog;
//		int priority;
		Status(int y, int x, BitSet map, int dog, boolean bfd){
			this.y = y; this.x = x;
			this.map = map;
			this.dog = dog;
			bfDog = bfd;
//			priority = pr;
		}
//		@Override
//		public int compareTo(Status s) {
//			return s.priority-priority;
//		}
	}
	Queue<Status> pq = new ArrayDeque<>();
//	BitSet[] qbs = new BitSet[H*W];
	int[][] bfr = new int[H][W];
//	boolean[] bfdq = new boolean[H*W];
//	int[] dgq = new int[H*W];
	Command searchNearItem(int[][] dist, int[] list, int n, int dep, int pid, boolean copy, boolean esc){
//		final int offset = dep==0?1:0;
		targetPos[pid] = -1;
		target[pid] = -1;
		for (int i = 0; i < H; ++i)
			Arrays.fill(dist[i], inf);
//		int qi = 0, qe = 0;
		int ddOld = 0;
		for(int i=0; i<n; i++){
			if(list[i]<0) continue;
			final int y = list[i]/W;
			final int x = list[i]%W;
			dist[y][x] = -1;
		}
		final int py = pos[pid]/W;
		final int px = pos[pid]%W;
		pq.clear();
		{
			final int y = py;
			final int x = px;
			pq.add(new Status(y, x, mapToBS(map, mss), 0, false));
//			qy[0] = y;
//			qx[0] = x;
//			bfdq[0] = false;
//			dgq[0] = 0;
			ddOld = dogDist[y][x];
			if(ddOld>0) ddOld = 1;
			dist[y][x] = 0;
//			qbs[0] = mapToBS(map, mss);
//			qe++;
		}
		culcEval();
		int ay = -1, ax = -1;
		int lastDog = 0;
//		int bestPriority = -inf;
//		BitSet lastMap = null;
		int best = esc||dogs<20?0:100;
		out: while (!pq.isEmpty()) {
			Status p = pq.poll();
			int y = p.y;
			int x = p.x;
			int dg = p.dog;
			boolean bfdg = p.bfDog;
			BitSet smap = p.map;
			for (int i = 0; i < 4; ++i) {
				final int ny = y+dy[i];
				final int nx = x+dx[i];
				final int nny = ny+dy[i];
				final int nnx = nx+dx[i];
				int ndog = dg;
				boolean bdg = false;
				if(dogDist[ny][nx]==0){
					if(dg>2 || bfdg) continue;
					bdg = true;
					ndog++;
				}
//				int priority = /*-dogDist[ny][nx]+*/sq(40-dist[ny][nx]);
//				if(dogs<30){
//					int tp = pos[pid^1];
//					final int iy = tp/W;
//					final int ix = tp%W;
//					priority += (Math.abs(iy-ny)+Math.abs(ix-nx))*2;
//				}
				// 岩の向こうの犬は無視していることに注意
				if(dist[ny][nx]==inf && !isWall(ny, nx, map)
						// 岩がないか、押せる岩
						&& (!get(ny, nx, smap) || !get(nny, nnx, smap) && !isWall(nny, nnx, map)
								&& ((dist[y][x]+1)>dep+1 && (dogs<30||!isDogInMap(nny, nnx, basemap)||getAroundDogs(nny, nnx, dogDist)<=3)
										|| !isStone(nny, nnx, map)/*注意*/ && !isNinja(nny, nnx, map) && !isDogInMap(nny, nnx, basemap)))
						&& (dist[y][x]>dep+1
								|| (dist[y][x]/2<oldDogDist[ny][nx])
								|| dep==2&&dist[y][x]==0
								|| copy&&esc&&(dist[y][x]+1!=dep+1||oldDogDist[ny][nx]>1)
								|| esc&&oldDogDist[ny][nx]>1)){
					dist[ny][nx] = dist[y][x]+1;
					bfr[ny][nx] = 3-i;
//					qy[qe] = ny;
//					qx[qe] = nx;
					BitSet newbs = (BitSet)smap.clone();
					if(get(ny, nx, smap)){
						clear(ny, nx, newbs);
						set(nny, nnx, newbs);
					}
//					qbs[qe] = newbs;
					if(/*bestPriority==-inf && */point[ny][nx]+dist[ny][nx]>best && dist(py,px,ny,nx)>=5 && dogDist[ny][nx]>0){
						best = point[ny][nx]+dist[ny][nx]+best;
						ay = ny;
						ax = nx;
//						lastMap = newbs;
					}

					pq.add(new Status(ny, nx, newbs, ndog, bdg));
//					++qe;
				}else if(dist[ny][nx]==-1
						// 石が置いてないか，動かせる
						&& (!get(ny, nx, smap) || !get(nny, nnx, smap) && !isWall(nny, nnx, map)
								// 距離2以上か，人や犬が石の奥にいない
								&& ((dist[y][x]+1)>dep+1 && (dogs<30||!isDogInMap(nny, nnx, basemap)||getAroundDogs(nny, nnx, dogDist)<=3)
										|| !isStone(nny, nnx, map)/*注意*/ && !isNinja(nny, nnx, map) && !isDogInMap(nny, nnx, basemap)))
						
						&& (dist[y][x]>dep+1 // 目的地が今回たどり着けない
								|| (dist[y][x]/2<oldDogDist[ny][nx]) // 犬から安全圏
								|| dep==2&&dist[y][x]==0 // 超加速かつ目の前
										// copyかつ行先に犬がいないか通過点である
								|| copy&&(dist[y][x]+1!=dep+1||oldDogDist[ny][nx]>1)
								/*|| esc&&dogDist[ny][nx]>0*/)
						//((dist[y][x]+2)/2<dogDist[ny][nx]  || copy&&esc&&dogDist[ny][nx]>0)
						/*&& priority>bestPriority*/){
					/*bestPriority = priority;*/
					ay = ny;
					ax = nx;
					lastDog = p.dog;
//					lastMap = smap;
//					if(get(ny, nx, smap)){
//						clear(ny, nx, lastMap);
//						set(nny, nnx, lastMap);
//					}
					bfr[ny][nx] = 3-i;
					dist[ny][nx] = dist[y][x]+1;
					target[pid] = xy2idxItem.get(ny*W+nx);
					targetPos[pid] = ny*W+nx;
					targetDist[pid] = dist[ny][nx];
					break out;
				}
			}
		}
		if(ay==-1){
			if(dep==0&&copy){
				int d = getNearEmptyCell(py, px, dogDist, dist);
				if(d!=-1) return new Command(d);
			}
			System.err.println("search failed");
			return null;
		}
//		int oay = ay;
//		int oax = ax;
//		dump(dist);
//		dist[ay][ax] = -3;
		int dst = dist[ay][ax];
//		final int oldDst = dst;
//		dump(bfr);
//		boolean reach = dst<=2;
//		int[] dir = {-1, -1};
		int[] dir = new int[dep+1];
		Arrays.fill(dir, -1);
		while(dist[ay][ax]!=0){
			dist[ay][ax] = -2;
			final int newy = ay+dy[bfr[ay][ax]];
			final int newx = ax+dx[bfr[ay][ax]];
			if(--dst<dir.length) dir[dst] = 3-bfr[ay][ax];
			ay = newy;
			ax = newx;
		}
//		String res = "";
		Command res = new Command();
		boolean moveStone = false;
		int getItem = 0;
		/**/
		for(int i=0; i<dir.length; i++){
			if(dir[i]==-1){
				Command add = searchNearItem(dist, list, n, dep-i, pid, copy, esc);
				if(add!=null){
					res.add(add);
					res.point += getItem*70-lastDog*3;
				}
				break;
			}
			removeNinja(ay, ax, pid, map);
			ay += dy[dir[i]];
			ax += dx[dir[i]];
			if(isStone(ay, ax, map)){
				removeStone(ay, ax, map);
				setStone(ay+dy[dir[i]], ax+dx[dir[i]], map);
				moveStone = true;
			}
			setNinja(ay, ax, pid, map, pos);
			if(isItem(ay, ax, map)){
				getItem++;
				removeFromItemDist(ay, ax, copy);
			}
			res.add(dir[i]);
			res.setPoint((target[pid]!=-1?50-dist[ay][ax]:0)+getItem*70-lastDog*3-getAroundDogs(ay, ax, oldDogDist)*10);
		}

		if(!copy && nextToDog(res.apply(py, px)) || copy && isDog(res.apply(py, px))) return null;
		res.moveStone |= moveStone;
		return res;
	}
	
	int getAroundDogs(int py, int px, int[][] dogDist){
		int count = 0;
		for(int i=0; i<8; i++){
			if(dogDist[py+dy8[i]][px+dx8[i]]==0) count++;
		}
		return count;
	}
	
	int sq(int a){
		return a*a;
	}
	
	int[][] itemEval = new int[H][W];
	int[][] dogEval = new int[H][W];
	// TODO
	void culcListEval(int[] list, int n, int[][] eval){
//		for (int i = 0; i < H; ++i)
//			Arrays.fill(dist[i], inf);
		int[][][] dist = new int[n][H][W];
		int maxD = H+W;
		for(int i=0; i<n; i++){
			for(int j=0; j<H; j++) Arrays.fill(dist[i][j], inf);
			int qi = 0, qe = 0;
			if(list[i]==-1) continue;
			final int ty = list[i]/W;
			final int tx = list[i]%W;
			qy[qe] = ty;
			qx[qe] = tx;
			dist[i][ty][tx] = 0;
			qe++;
			while (qi < qe) {
				int y = qy[qi], x = qx[qi];
				if(dist[i][y][x]>=inf){
					System.err.println("INF");
				}
				++qi;
				for (int d = 0; d < 4; ++d) {
					int ny = y+dy[d];
					int nx = x+dx[d];
					if (dist[i][ny][nx] == inf && !isWall(ny, nx, map)) {
						dist[i][ny][nx] = dist[i][y][x] + 1;
						qy[qe] = ny;
						qx[qe] = nx;
//						maxD = Math.max(maxD, dist[i][ny][nx]);
						++qe;
					}
				}
			}
		}
		for(int i=0; i<H; i++){
			for(int j=0; j<W; j++){
				eval[i][j] = 0;
				for(int k=0; k<n; k++){
					eval[i][j] += sq(maxD-dist[k][i][j]);
				}
				eval[i][j] /= 100;
			}
		}
	}
	
	int getNearEmptyCell(int y, int x, int[][] dogDist, int[][] itemDist){
		int best = inf+1;
		int bestID = -1;
		for(int i=0; i<4; i++){
			final int ny = y+dy[i];
			final int nx = x+dx[i];
			if(isDog(ny, nx, dogMap) || !okMove(y, x, i, map, dogDist)) continue;
			if(itemDist[ny][nx]<best){
				best = itemDist[ny][nx];
				bestID = i;
			}
		}
		return bestID;
	}
	
	boolean isDog(int pos){
		return dogDist[pos/W][pos%W]==0;
	}
	
	boolean nextToDog(int pos){
		return dogDist[pos/W][pos%W]<=1;
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
		if(idx<0) return;
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
	
	class Pos implements Comparable<Pos>{
		int[] p;
		int pow;
		BitSet stone;
		int[] dog;
		int[] item;
		int point;
		int hash;
		int command;
		Pos(int[] p, int pow, int[] dog, BitSet stone, int[] item, int point){
			this.p = p;
			this.pow = pow;
			this.point = point;
			this.dog = dog;
			this.stone = stone;
		}
		@Override
		public int compareTo(Pos p) {
			if(p.point != point) return p.point-point;
			return hash-p.hash;
		}
	}
	TreeSet<Pos> bq = new TreeSet<>();
	final int SEARCH_DEP = 10;
	final int BEAM_WID = 10;
	Pos[] tmpArray = new Pos[BEAM_WID];
	CommandSet beamSearch(int[] pos, int[] map, int[][] itemDist){
		int[] itemDistLine = new int[H*W];
		for(int i=0; i<H; i++) for(int j=0; j<W; j++) itemDistLine[i*W+j] = itemDist[i][j];
		bq.clear();
		bq.add(new Pos(pos, pow, dog.clone(), mapToBS(map, mss), itemDistLine, pow*10-distSum(itemDistLine, pos)));
		int[][] d = new int[2][2];
//		int[] d2 = new int[2];
		for(int i=0; i<SEARCH_DEP; i++){
			int sz = 0;
			while(!bq.isEmpty()) tmpArray[sz++] = bq.pollFirst();
			for(int j=0; j<sz; j++){
				Pos p = tmpArray[j];
				for(d[0][0]=0; d[0][0]<5; d[0][0]++){
					for(d[0][1]=0; d[0][1]<5; d[0][1]++){
						for(d[1][0]=0; d[1][0]<5; d[1][0]++){
							for(d[1][1]=0; d[1][1]<5; d[1][1]++){
								for(int c=0; c<2; c++){
									Pos np = simulate(p, d, c==1);
									if(np != null){
										if(i==0){
											np.command |= d[1][1]<<9|d[1][0]<<6|d[0][1]<<3|d[0][0];
											if(c==1) np.command |= 1<<13;
										}
										// 先のコマンドが下位，ninja[0]のコマンドが下位
										bq.add(np);
										if(bq.size()>BEAM_WID) bq.pollLast();
									}
								}
							}
						}
					}
				}
			}
		}
		int command = bq.pollFirst().command;
		Command[] com = new Command[2];
		com[0] = new Command(command&7, (command>>3)&7);
		com[1] = new Command((command>>6)&7, (command>>9)&7);
		String skill = null;
		int skillCost = 0;
		if((command&1<<13)>0){
			final int fp = getFarPos(sdist, pos, 2, mapToBS(map, mss));
			final int y = fp/W;
			final int x = fp%W;
			skill = SK_COPY_ME+" "+y+" "+x;
			skillCost = cost[SK_COPY_ME];
		}
		return new CommandSet(com, skill, skillCost);
	}
	int[] y = new int[2];
	int[] x = new int[2];
	Pos simulate(Pos p, int[][] d, boolean copy){
		for(int i=0; i<2; i++){
			y[i] = p.p[i]/W;
			x[i] = p.p[i]%W;
		}
		int pow = p.pow;
		if(copy){
			pow -= cost[SK_COPY_ME];
			if(pow<0) return null;
		}
		BitSet map = (BitSet)p.stone.clone();
		BitSet dogMap = new BitSet(H*W);
		int getItem = 0;
		for(int i=0; i<p.dog.length; i++) dogMap.set(p.dog[i]);
		for(int pid=0; pid<2; pid++){
			for(int i=0; i<2; i++){
				y[pid] += dy[d[pid][i]];
				x[pid] += dx[d[pid][i]];
				final int pos = y[pid]*W+x[pid];
				// 壁か犬特攻なら没
				if(isWall(y[pid], x[pid], basemap) || dogMap.get(pos)) return null;
				// 正面が石のとき
				if(map.get(pos)){
					final int ny = y[pid]+dy[d[pid][i]];
					final int nx = x[pid]+dx[d[pid][i]];
					// 押せるかチェック
					if(isWall(ny, nx, basemap)				// 壁か
							|| map.get(ny*W+nx)				// 石か
							|| y[pid^1]==ny&&x[pid^1]==nx	// 味方か
							|| dogMap.get(ny*W+nx))			// 犬か
						return null;
					// 動かす
					map.clear(pos);
					map.set(ny*W+nx);
				}
				if(p.item[pos]==0){
					getItem++;
					p.item[pos] = -1;
				}
			}
			for(int i=0; i<4; i++){
				if(dogMap.get((y[pid]+dy[i])*W+x[pid]+dx[i])) return null;
			}
		}
		int fp = getFarPos(sdist, p.p, 2, map);
		int[] dog = p.dog.clone();
		if(copy){
			getFarPos(sdist, new int[]{fp}, 1, map);
			simulateDogs(map, dog, dogMap);
		}else{
			simulateDogs(map, dog, dogMap);
		}
		// pointをどうするか．．．
		if(getItem>0){
			// itemDistの更新
			int[] newItemDist = p.item.clone();
			bfsItem(newItemDist, map, dogMap);
			int[] newpos = {y[0]*W+x[0], y[1]*W+x[1]};
			return new Pos(newpos, pow+2*getItem, dog, map, newItemDist, pow*10+getItem*5-distSum(newItemDist, newpos));
		}else{
			int[] newpos = {y[0]*W+x[0], y[1]*W+x[1]};
			return new Pos(newpos, pow, dog, map, p.item, pow*10-distSum(p.item, newpos));
		}
	}
	
	int distSum(int[] dist, int[] pos){
		return dist[pos[0]]+dist[pos[1]];
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
