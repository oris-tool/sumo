package org.oristool.sumo.utils;

import au.com.bytecode.opencsv.CSVWriter;
import de.tudresden.sumo.cmd.*;
import de.tudresden.ws.container.SumoStringList;
import de.tudresden.ws.container.SumoVehicleData;
import it.polito.appeal.traci.SumoTraciConnection;
import org.oristool.sumo.VaryingParamsSolutionPlotter;
import org.oristool.sumo.samplegenerators.ShiftedExpSampleGenerator;
import org.oristool.sumo.samplegenerators.UniformSampleGenerator;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class VaryingParamsSumoAnalyzer {

    private double[] availability;
    private double[] onQueueCars;

    public VaryingParamsSumoAnalyzer analyze(int flowIndex,
                                             double INV_STEP_LENGTH,
                                             int SIMULATION_STEPS,
                                             double[][] t1_arrivalTimesPerRun,
                                             double[][] t2_arrivalTimesPerRun,
                                             boolean[] redTrafficLightPattern,
                                             CSVWriter queueFileWriter) {

        boolean[] redTrafficLightPatternCopy = Arrays.copyOf(redTrafficLightPattern, redTrafficLightPattern.length);

        this.availability = new double[SIMULATION_STEPS];
        this.onQueueCars = new double[SIMULATION_STEPS];
        String[] onQueueCars4Run = new String[SIMULATION_STEPS];

        for (int q = 0; q < VaryingParamsSolutionPlotter.sumo_runs; q++) {
            System.out.print("r");
        }

        for (int r = 0; r < VaryingParamsSolutionPlotter.sumo_runs; r++) {

            redTrafficLightPattern = Arrays.copyOf(redTrafficLightPatternCopy, redTrafficLightPatternCopy.length);

            try {
                configureEnvironment(flowIndex);
            } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException
                     | TransformerException e) {
                e.printStackTrace();
            }

            /*
             * Tempi di arrivo per le auto.
             */
            double lastCarTime = 0.;
            ArrayList<Integer> carArrivalSteps = new ArrayList<>();
            {
                final int timeSlotIndex = 0;
                final double slotTimeBound = VaryingParamsSolutionPlotter.timeBound1.doubleValue();

                final BigDecimal originalSlotRate = VaryingParamsSolutionPlotter.arrivalRates.get(flowIndex).get(timeSlotIndex);
                final BigDecimal newSlotRate = BigDecimal.ONE.divide(BigDecimal.ONE.divide(originalSlotRate,
                        RoundingMode.HALF_UP).subtract(VaryingParamsSolutionPlotter.sumo_shift), 5, RoundingMode.HALF_UP);
                ShiftedExpSampleGenerator shiftedExponentialSampler = new ShiftedExpSampleGenerator(newSlotRate,
                        VaryingParamsSolutionPlotter.sumo_shift);
                while (lastCarTime <= slotTimeBound) {
                    double sample = shiftedExponentialSampler.getSample().doubleValue();
                    lastCarTime += sample;
                    carArrivalSteps.add((int) (lastCarTime * INV_STEP_LENGTH));
                }
            }
            {
                final int timeSlotIndex = 1;
                final double slotTimeBound = VaryingParamsSolutionPlotter.timeBound2.doubleValue();

                final BigDecimal originalSlotRate = VaryingParamsSolutionPlotter.arrivalRates.get(flowIndex).get(timeSlotIndex);
                final BigDecimal newSlotRate = BigDecimal.ONE.divide(BigDecimal.ONE.divide(originalSlotRate,
                        RoundingMode.HALF_UP).subtract(VaryingParamsSolutionPlotter.sumo_shift), 5, RoundingMode.HALF_UP);
                ShiftedExpSampleGenerator shiftedExponentialSampler = new ShiftedExpSampleGenerator(newSlotRate,
                        VaryingParamsSolutionPlotter.sumo_shift);
                while (lastCarTime <= slotTimeBound) {
                    double sample = shiftedExponentialSampler.getSample().doubleValue();
                    lastCarTime += sample;
                    carArrivalSteps.add((int) (lastCarTime * INV_STEP_LENGTH));
                }
            }
            {
                final int timeSlotIndex = 2;
                final double slotTimeBound = VaryingParamsSolutionPlotter.timeBound3.doubleValue();

                final BigDecimal originalSlotRate = VaryingParamsSolutionPlotter.arrivalRates.get(flowIndex).get(timeSlotIndex);
                final BigDecimal newSlotRate = BigDecimal.ONE.divide(BigDecimal.ONE.divide(originalSlotRate,
                        RoundingMode.HALF_UP).subtract(VaryingParamsSolutionPlotter.sumo_shift), 5, RoundingMode.HALF_UP);
                ShiftedExpSampleGenerator shiftedExponentialSampler = new ShiftedExpSampleGenerator(newSlotRate,
                        VaryingParamsSolutionPlotter.sumo_shift);
                while (lastCarTime <= slotTimeBound) {
                    double sample = shiftedExponentialSampler.getSample().doubleValue();
                    lastCarTime += sample;
                    carArrivalSteps.add((int) (lastCarTime * INV_STEP_LENGTH));
                }
            }
            {
                final int timeSlotIndex = 3;
                final double slotTimeBound = VaryingParamsSolutionPlotter.timeBound.doubleValue();

                final BigDecimal originalSlotRate = VaryingParamsSolutionPlotter.arrivalRates.get(flowIndex).get(timeSlotIndex);
                final BigDecimal newSlotRate = BigDecimal.ONE.divide(BigDecimal.ONE.divide(originalSlotRate,
                        RoundingMode.HALF_UP).subtract(VaryingParamsSolutionPlotter.sumo_shift), 5, RoundingMode.HALF_UP);
                ShiftedExpSampleGenerator shiftedExponentialSampler = new ShiftedExpSampleGenerator(newSlotRate,
                        VaryingParamsSolutionPlotter.sumo_shift);
                while (lastCarTime <= slotTimeBound) {
                    double sample = shiftedExponentialSampler.getSample().doubleValue();
                    lastCarTime += sample;
                    carArrivalSteps.add((int) (lastCarTime * INV_STEP_LENGTH));
                }
            }

            // Start simulation
            SumoTraciConnection conn = new SumoTraciConnection(VaryingParamsSolutionPlotter.WIN_SUMO_BIN,
                    VaryingParamsSolutionPlotter.CONFIG_FILE);
            conn.addOption("step-length", VaryingParamsSolutionPlotter.sumo_time_step.toString());
            conn.addOption("start", "1"); // Start Sumo immediately.
            try {
                conn.runServer();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int frstTramCounter = 0;
            int scndTramCounter = 0;
            int carCounter = 0;
            HashSet<String> detectedCars = new HashSet<>();
            Arrays.fill(onQueueCars4Run, null);

            for (int ts = 0; ts < SIMULATION_STEPS; ts++) {

                /*
                 * Imposta il semaforo delle auto secondo il pattern
                 * redTrafficLightPattern[ts] == true    -->   phase 0 (red)
                 * redTrafficLightPattern[ts] == false   -->   phase 1 (green)
                 *
                 * L'array di booleani è modificato dinamicamente all'interno del ciclo (vedi codice inserimento tram):
                 * il semaforo è impostato a rosso anche per tutto il periodo necessario al passaggio di un tram,
                 * anche se era previsto verde per il pattern.
                 */
                try {
                    conn.do_job_set(Trafficlight.setPhase("crossRoadTL", redTrafficLightPattern[ts] ? 0 : 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Avanza la simulazione di uno step temporale
                try {
                    conn.do_timestep();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*
                 * Immette nella rete un'auto o un tram se lo step attuale corrisponde ad uno di
                 * quelli previsti per l'ingresso di un veicolo.
                 */

                if (ts >= carArrivalSteps.get(carCounter)) {
                    SumoVehicleData sensorCarInStart;
                    try {
                        sensorCarInStart = (SumoVehicleData) conn
                                .do_job_get(Inductionloop.getVehicleData("sensorCarInStart"));
                        if (sensorCarInStart == null || sensorCarInStart.ll.size() == 0) {
                            conn.do_job_set(Vehicle.addFull("CarID_" + carCounter, "carRoute", "car", "now", "0", "0",
                                    "max", "current", "max", "current", "", "", "", 0, 0));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    carCounter++;
                }

                if (ts >= t1_arrivalTimesPerRun[r][frstTramCounter]) {
                    try {
                        conn.do_job_set(Vehicle.addFull("TramID_" + frstTramCounter, "tram1Route", "tramway", "now",
                                "0",
                                "0",
                                new BigDecimal("100")
                                        .divide(new BigDecimal(VaryingParamsSolutionPlotter.t1_crosslightAntTime),
                                                RoundingMode.HALF_UP)
                                        .toPlainString(), "current", "max", "current", "", "",
                                "", 0, 0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    frstTramCounter++;
                }

                if (ts >= t2_arrivalTimesPerRun[r][scndTramCounter]) {
                    try {
                        conn.do_job_set(Vehicle.addFull("Tram2ID_" + scndTramCounter, "tram2Route", "tramway", "now",
                                "0",
                                "0",
                                new BigDecimal("100")
                                        .divide(new BigDecimal(VaryingParamsSolutionPlotter.t2_crosslightAntTime),
                                                RoundingMode.HALF_UP)
                                        .toPlainString(), "current", "max", "current", "", "",
                                "", 0, 0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    scndTramCounter++;
                }

                /*
                 * Se ci sono tram su ogni linea, imposta il semaforo a rosso per l'istante temporale successivo
                 */

                SumoStringList tram1InEdge_0_list = null;
                SumoStringList tram2InEdge_0_list = null;
                try {
                    tram1InEdge_0_list = (SumoStringList) conn.do_job_get(Lane.getLastStepVehicleIDs("tram1InEdge_0"));
                    tram2InEdge_0_list = (SumoStringList) conn.do_job_get(Lane.getLastStepVehicleIDs("tram2InEdge_0"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if ((tram1InEdge_0_list != null && tram1InEdge_0_list.size() > 0)
                        || (tram2InEdge_0_list != null && tram2InEdge_0_list.size() > 0)) {
                    if (ts + 1 < redTrafficLightPattern.length)
                        redTrafficLightPattern[ts + 1] = true;
                }

                /*
                 * Se c'è un tram sul sensore all'incrocio, ne imposta il tempo di
                 * attraversamento. In realtà imposta solo la durata del
                 * semaforo rosso (il tram se ne va per i fatti suoi), ma va bene anche così.
                 */

                SumoVehicleData sensorTram1Crossroad = null;
                try {
                    sensorTram1Crossroad = (SumoVehicleData) conn
                            .do_job_get(Inductionloop.getVehicleData("sensorTram1Crossroad"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (sensorTram1Crossroad != null && sensorTram1Crossroad.ll != null && sensorTram1Crossroad.ll.size() > 0) {
                    try {
                        conn.do_job_set(Trafficlight.setPhase("crossRoadTL", 0));
                        double redDuration = new UniformSampleGenerator(new BigDecimal(VaryingParamsSolutionPlotter.t1_leavingEFTime),
                                new BigDecimal(VaryingParamsSolutionPlotter.t1_leavingLFTime))
                                .getSample().setScale(2, RoundingMode.HALF_DOWN).doubleValue();
                        for (int i = 0; i < redDuration * INV_STEP_LENGTH; i++) {
                            if (ts + i < redTrafficLightPattern.length)
                                redTrafficLightPattern[ts + i] = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                SumoVehicleData sensorTram2Crossroad = null;
                try {
                    sensorTram2Crossroad = (SumoVehicleData) conn.do_job_get(Inductionloop.getVehicleData("sensorTram2Crossroad"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (sensorTram2Crossroad != null && sensorTram2Crossroad.ll != null && sensorTram2Crossroad.ll.size() > 0) {
                    try {
                        conn.do_job_set(Trafficlight.setPhase("crossRoadTL", 0));
                        double redDuration = new UniformSampleGenerator(new BigDecimal(VaryingParamsSolutionPlotter.t2_leavingEFTime),
                                new BigDecimal(VaryingParamsSolutionPlotter.t2_leavingLFTime))
                                .getSample().setScale(2, RoundingMode.HALF_DOWN).doubleValue();
                        for (int i = 0; i < redDuration * INV_STEP_LENGTH; i++) {
                            if (ts + i < redTrafficLightPattern.length)
                                redTrafficLightPattern[ts + i] = true;
                        }
                        //conn.do_job_set(Trafficlight.setPhaseDuration("crossRoadTL", redDuration));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                /*
                 * Controllo se l'auto è stata immessa
                 */

                SumoVehicleData sensorCarInStart;
                try {
                    sensorCarInStart = (SumoVehicleData) conn.do_job_get(Inductionloop.getVehicleData("sensorCarInStart"));
                    if (sensorCarInStart != null && sensorCarInStart.ll.size() > 0) {
                        for (SumoVehicleData.VehicleData c : sensorCarInStart.ll) {
                            detectedCars.add(c.vehID);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*
                 * Verifica dello stato del semaforo per la misurazione dell'availability
                 */

                String tlPhase = null;
                try {
                    tlPhase = (String) conn.do_job_get(Trafficlight.getRedYellowGreenState("crossRoadTL"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if ("rGr".equals(tlPhase)) {
                    this.availability[ts] += (1. / (double) VaryingParamsSolutionPlotter.sumo_runs);
                }

                /*
                 * Conteggio delle auto in coda ad ogni step.
                 */

                SumoStringList queueList = null;
                try {
                    queueList = (SumoStringList) conn.do_job_get(Edge.getLastStepVehicleIDs("carInEdge"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                BigInteger carCounters = BigInteger.ZERO;
                assert queueList != null;
                carCounters = carCounters.add(BigInteger.valueOf(queueList.size()));

                onQueueCars4Run[ts] = Double.toString(carCounters.doubleValue());
                this.onQueueCars[ts] += carCounters.doubleValue() / (double) VaryingParamsSolutionPlotter.sumo_runs;

            }
            conn.close();

            queueFileWriter.writeNext(onQueueCars4Run);

            try {
                Runtime.getRuntime().exec("taskkill /F /IM " + VaryingParamsSolutionPlotter.SUMO_EXE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.print("\b");
        }

        return this;
    }

    private void configureEnvironment(int flowIndex) throws XPathExpressionException, ParserConfigurationException,
            SAXException, IOException, TransformerException {
        VaryingParamsSumoConfigurator.changeRoadLenght(flowIndex);
        VaryingParamsSumoConfigurator.configureNetwork(flowIndex);
        VaryingParamsSumoConfigurator.configureVehicles(flowIndex);
    }

    public double[] getOnQueueCars() {
        return this.onQueueCars;
    }

    public double[] getAvailability() {
        return this.availability;
    }

    public double getMaxCarsOnQueue() {
        return Arrays.stream(this.onQueueCars).max().getAsDouble();
    }
}
