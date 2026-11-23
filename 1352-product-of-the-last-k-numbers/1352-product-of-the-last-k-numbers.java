class ProductOfNumbers {
    private List<Integer> pre;

    public ProductOfNumbers() {
        pre = new ArrayList<>();
        pre.add(1);
    }

    public void add(int num) {
        if (num == 0) {
            pre = new ArrayList<>();
            pre.add(1);
        } else {
            pre.add(pre.get(pre.size() - 1) * num);
        }
    }

    public int getProduct(int k) {
        int n = pre.size() - 1;
        if (k > n) return 0;
        return pre.get(n) / pre.get(n - k);
    }
}