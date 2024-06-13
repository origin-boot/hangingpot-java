package com.origin.hangingpot.domain.event;

import com.origin.hangingpot.domain.ScheduleJob;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class DummyEvent extends ScheduleJob {


}
