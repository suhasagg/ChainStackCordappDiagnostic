package com.nodediagnostic;

import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;

import java.util.Arrays;
import java.util.List;

import net.corda.core.identity.Party;

// *********
// * State *
// *********
public class IOUState implements ContractState {
    private final int value;
    private final Party lender;
    private final Party borrower;

    public IOUState(int value, Party lender, Party borrower) {
        this.value = value;
        this.lender = lender;
        this.borrower = borrower;
    }

    public int getValue() {
        return value;
    }

    public Party getLender() {
        return lender;
    }

    public Party getBorrower() {
        return borrower;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(lender, borrower);
    }
}