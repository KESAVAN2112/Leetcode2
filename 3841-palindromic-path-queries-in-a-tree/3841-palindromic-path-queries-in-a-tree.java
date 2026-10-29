class Solution {
    List<List<Integer>>graph;

    public List<Boolean> palindromePath(int n, int[][] edges, String s, String[] queries) {
        buildGraph(n, edges);
        HLD hld = new HLD(graph, n, s);

        hld.dfs(0, -1);
        hld.decompose(0, 0);

        SegmentTree segTree = new SegmentTree(n);
        segTree.build(0, 0, n-1, hld.baseArray);

        List<Boolean>result = new ArrayList<>();

        for(String q : queries){
            String[] part = q.split(" ");
            if(part[0].equals("update")){
                int idx = Integer.parseInt(part[1]);
                int val = (1 << (part[2].charAt(0)-'a'));
                segTree.update(0, 0, n-1, hld.pos[idx], val);
            }else{
                int u = Integer.parseInt(part[1]);
                int v = Integer.parseInt(part[2]);
                int res = hld.query(u, v, segTree);

                boolean value = ((res & (res-1)) == 0);

                result.add(value);
            }
        }

        return result;
    }
    public void buildGraph(int n, int[][]edges){
        graph = new ArrayList<>();
        for(int i=0; i<n; i++){
            graph.add(new ArrayList<>());
        }
        for(int[]edge : edges){
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }
    }

}

class SegmentTree{
    int[]tree;
    int n;
    public SegmentTree(int n){
        tree = new int[4*n];
    }
    public void build(int idx, int start, int end, int[]arr){
        if(start == end){
            tree[idx] = arr[start];
            return;
        }
        int leftIdx = 2*idx + 1;
        int rightIdx = 2*idx + 2;
        int mid = (start + end)/2;
        build(leftIdx, start, mid, arr);
        build(rightIdx, mid+1, end, arr);

        tree[idx] = tree[leftIdx] ^ tree[rightIdx];
    }

    public void update(int node, int start, int end, int idx, int val){
        if(start == end){
            tree[node] = val;
            return;
        }
        int mid = (start + end)/2;
        int leftIdx = 2*node + 1;
        int rightIdx = 2*node + 2;

        if(idx <= mid){
            update(leftIdx, start, mid, idx, val);
        }else{
            update(rightIdx, mid+1, end, idx, val);
        }

        tree[node] = tree[leftIdx] ^ tree[rightIdx];
    }

    public int query(int idx, int left, int right, int start, int end){
        if(right < start || end < left) return 0;

        if(left <= start && end <= right) return tree[idx];

        int leftIdx = 2*idx + 1;
        int rightIdx = 2*idx + 2;
        int mid = (start + end)/2;

        int l = query(leftIdx, left, right, start, mid);
        int r= query(rightIdx, left, right, mid+1, end);

        return l^r;
    }
}

class HLD{
    List<List<Integer>>graph;
    int[]parent;
    int[]depth;
    int[]heavy;
    int[]head;
    int[]pos;
    int[]subtreeSize;
    int[]baseArray;
    String str;

    int currentPosition = 0;

    public HLD(List<List<Integer>>graph, int n, String str){
        this.graph = graph;
        this.parent = new int[n];
        this.depth = new int[n];
        this.heavy = new int[n];
        this.head = new int[n];
        this.pos = new int[n];
        this.subtreeSize = new int[n];
        this.baseArray = new int[n];
        this.str = str;


        Arrays.fill(this.heavy, -1);
    }

    public int dfs(int node, int par){
        parent[node] = par;
        int subTree = 1;
        int maxSubTree = 0;
        for(int child : graph.get(node)){
            if(child==par) continue;
            depth[child] = 1 + depth[node];
            int childSubTree = dfs(child, node);
            subTree += childSubTree;

            if(childSubTree > maxSubTree){
                maxSubTree = childSubTree;
                heavy[node] = child;
            }
        }

        subtreeSize[node] = subTree;
        return subTree;
    }
    public void decompose(int node, int chainHead){
        head[node] = chainHead;
        pos[node] = currentPosition;
        baseArray[currentPosition] = (1 << (str.charAt(node)-'a'));

        currentPosition++;
        
        if(heavy[node]!=-1){
            decompose(heavy[node], chainHead);
        }

        for(int child : graph.get(node)){
            if(child == parent[node] || heavy[node]==child) continue;
            decompose(child, child);
        }
    }
    public int query(int u, int v, SegmentTree segTree){
        int res = 0;
        int n = graph.size();
        while(head[u] != head[v]){
            if(depth[head[u]] < depth[head[v]]){
                int temp = v;
                v = u;
                u = temp;
            }

            int chainHead = head[u];
            res ^= segTree.query(0, pos[chainHead], pos[u], 0, n-1);
            u = parent[chainHead];
        }
        if(depth[u] > depth[v]){
            int temp = v;
            v = u;
            u = temp;
        }

        res ^= segTree.query(0, pos[u], pos[v], 0, n-1);

        return res;
    }
}