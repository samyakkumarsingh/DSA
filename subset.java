class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> a=new ArrayList<>();
        List<Integer> op=new ArrayList<>();
        solve(nums,op,a,0);
        return a;
    }

    public void solve(int[] ip,List<Integer> op,List<List<Integer>> a,int i){
        if(i==ip.length){
             a.add(op);
             return;
        }
            
        solve(ip,new ArrayList<>(op),a,i+1); //not pick
        op.add(ip[i]);
        solve(ip,new ArrayList<>(op),a,i+1); //pick

    
    }
}
