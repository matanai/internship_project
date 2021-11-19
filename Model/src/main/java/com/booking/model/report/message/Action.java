package com.booking.model.report.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class Action
{
    private final String actionText;
    private final ActionColor actionColor;

    public enum ActionColor
    {
        ADD("#009933"), // TODO If using Angular, we dont need enums. Simply use string titles
        REMOVE("#ff6600"),
        UPDATE("#0066ff");

        private final String value;

        ActionColor(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
