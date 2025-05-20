package se.yrgo.advice;

import org.aspectj.lang.ProceedingJoinPoint;

public class PerformanceTimingAdvice {
    public Object performanceTimingMeasurement(ProceedingJoinPoint pjp) throws Throwable {

        long start = System.currentTimeMillis();

        try {
            return pjp.proceed();
        }finally {
            long end = System.currentTimeMillis();
            long time = end - start;
            System.out.println("Time taken for the method " + pjp.getSignature().getName()
                    + " from the class " + pjp.getSignature().getDeclaringTypeName() + " took " + time + " ms");
        }
    }
}
