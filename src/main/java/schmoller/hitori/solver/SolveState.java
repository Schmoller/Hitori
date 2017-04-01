package schmoller.hitori.solver;

public enum SolveState {
    /**
     * Used only for step by step solving
     */
    Unsolved,
    /**
     * The board is solved
     */
    Solved,
    /**
     * The board cannot be solved without violating
     * one of the rules
     */
    Invalid,
    /**
     * The board does not have a unique solution
     */
    NotUnique
}
