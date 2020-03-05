package com.gibbon.rpm;

import com.gibbon.rpm.annotation.ParentRedPoint;
import com.gibbon.rpm.annotation.RedPoint;
import com.gibbon.rpm.annotation.RedPointCanShow;
import com.gibbon.rpm.annotation.RedPointCanShowArrays;
import com.gibbon.rpm.annotation.RedPointNum;
import com.gibbon.rpm.annotation.RedPointNumArrays;
import com.gibbon.rpm.annotation.RedPointShowNum;
import com.gibbon.rpm.annotation.RedPointShowNumArrays;
import com.gibbon.rpm.annotation.RelativeRedPoint;
import com.gibbon.rpm.util.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author zhipeng.zhuo
 * @date 2020-03-04
 */
public class ServiceMethod {
    private String[] redPoints;
    private String parentRedPointId;
    private HashMap<String, String> relativeRedPointIds;
    private HashMap<String, Boolean> showNumMap;
    private boolean[] showNumsArrays;
    private HashMap<String, Integer> numMap;
    private int[] numsArrays;
    private HashMap<String, Boolean> canShowMap;
    private boolean[] canShowArrays;

    public ServiceMethod(String[] redPoints, String parentRedPointId, HashMap<String, String> relativeRedPointIds) {
        this(redPoints, parentRedPointId, relativeRedPointIds,  null, null, null, null);
    }

    public ServiceMethod(String[] redPoints, String parentRedPointId, HashMap<String, String> relativeRedPointIds, HashMap<String, Boolean> showNumMap, boolean[] showNumsArrays, HashMap<String, Integer> numMap, int[] numsArrays) {
        this(redPoints, parentRedPointId, relativeRedPointIds, showNumMap, showNumsArrays, numMap, numsArrays, null, null);
    }

    public ServiceMethod(String[] redPoints, String parentRedPointId, HashMap<String, String> relativeRedPointIds, HashMap<String, Boolean> showNumMap, boolean[] showNumsArrays, HashMap<String, Integer> numMap, int[] numsArrays, HashMap<String, Boolean> canShowMap, boolean[] canShowArrays) {
        this.redPoints = redPoints;
        this.parentRedPointId = parentRedPointId;
        this.relativeRedPointIds = relativeRedPointIds;
        this.showNumMap = showNumMap;
        this.showNumsArrays = showNumsArrays;
        this.numMap = numMap;
        this.numsArrays = numsArrays;
        this.canShowMap = canShowMap;
        this.canShowArrays = canShowArrays;
    }

    public String[] getRedPoints() {
        return redPoints;
    }

    public String getParentRedPointId() {
        return parentRedPointId;
    }

    public HashMap<String, String> getRelativeRedPointIds() {
        return relativeRedPointIds;
    }

    public HashMap<String, Boolean> getShowNumMap() {
        return showNumMap;
    }

    public boolean[] getShowNumsArrays() {
        return showNumsArrays;
    }

    public HashMap<String, Integer> getNumMap() {
        return numMap;
    }

    public int[] getNumsArrays() {
        return numsArrays;
    }

    public HashMap<String, Boolean> getCanShowMap() {
        return canShowMap;
    }

    public boolean[] getCanShowArrays() {
        return canShowArrays;
    }

    static final class Builder {
        private Method method;
        private Annotation[] methodAnnotations;
        Type[] parameterTypes;
        Annotation[][] parameterAnnotationsArray;
        private String[] redPoints;
        private String parentRedPointId;
        private HashMap<String, String> relativeRedPointIds;
        private HashMap<String, Boolean> showNumMap;
        private boolean[] showNumsArrays;
        private HashMap<String, Integer> numMap;
        private int[] numsArrays;
        private HashMap<String, Boolean> canShowMap;
        private boolean[] canShowArrays;
        private Object[] args;
        public Builder(Method method, Object[] args) {
            this.method = method;
            this.args = args;
            this.methodAnnotations = method.getAnnotations();
            this.parameterTypes = method.getParameterTypes();
            this.parameterAnnotationsArray = method.getParameterAnnotations();
        }

        public Builder() {

        }

        public Builder setRedPointIds(String[] redPoints) {
            this.redPoints = redPoints;
            return this;
        }

        public Builder setParentRedPointId(String parentRedPointId) {
            this.parentRedPointId = parentRedPointId;
            return this;
        }

        public Builder setRelativeRedPointIds(String[] relativeRedPointIds) {
            parseRelativeRedPointArrays(relativeRedPointIds);
            return this;
        }

        public Builder setRelativeRedPointIds(HashMap<String, String> relativeRedPointIds) {
            this.relativeRedPointIds = relativeRedPointIds;
            return this;
        }


        public Builder setShowNumMap(HashMap<String, Boolean> showNumMap) {
            this.showNumMap = showNumMap;
            return this;
        }

        public Builder setShowNumsArrays(boolean[] showNumsArrays) {
            this.showNumsArrays = showNumsArrays;
            return this;
        }

        public Builder setNumMap(HashMap<String, Integer> numMap) {
            this.numMap = numMap;
            return this;
        }

        public Builder setNumsArrays(int[] numsArrays) {
            this.numsArrays = numsArrays;
            return this;
        }

        public Builder setCanShowMap(HashMap<String, Boolean> canShowMap) {
            this.canShowMap = canShowMap;
            return this;
        }

        public Builder setCanShowArrays(boolean[] canShowArrays) {
            this.canShowArrays = canShowArrays;
            return this;
        }

        public ServiceMethod build() {
            if (methodAnnotations != null) {
                for (Annotation annotation : methodAnnotations) {
                    parseMethodAnnotation(annotation);
                }
            }


            if (redPoints == null || redPoints.length <= 0) {
                throw methodError("RedPoints should not be empty...");
            }


            if (parameterAnnotationsArray != null) {
                int parameterCount = parameterAnnotationsArray.length;

                for (int p = 0; p < parameterCount; p++) {
                    Type parameterType = parameterTypes[p];
                    if (Utils.hasUnresolvableType(parameterType)) {
                        throw parameterError(p, "Parameter type must not include a type variable or wildcard: %s",
                                parameterType);
                    }

                    Annotation[] parameterAnnotations = parameterAnnotationsArray[p];
                    if (parameterAnnotations == null) {
                        throw parameterError(p, "No RPM annotation found.");
                    }

                    parseParameterAnnotations(p, parameterAnnotations);
                }

                if (parameterCount > 0) {
                    if (showNumsArrays != null && redPoints.length != showNumsArrays.length) {
                        throw methodError("@RedPointShowNumArrays should be same size with @RedPoint");
                    }

                    if (numsArrays != null && redPoints.length != numsArrays.length) {
                        throw methodError("@RedPointNumArrays should be same size with @RedPoint");
                    }

                    if (canShowArrays != null && redPoints.length != canShowArrays.length) {
                        throw methodError("@RedPointNumArrays should be same size with @RedPoint");
                    }
                }
            } else {
                if (showNumsArrays != null && redPoints.length != showNumsArrays.length) {
                    throw methodError("showNumsArrays should be same size with redPoint");
                }

                if (numsArrays != null && redPoints.length != numsArrays.length) {
                    throw methodError("numsArrays should be same size with redPoint");
                }

                if (canShowArrays != null && redPoints.length != canShowArrays.length) {
                    throw methodError("canShowArrays should be same size with redPoint");
                }
            }


            return new ServiceMethod(this.redPoints, this.parentRedPointId, this.relativeRedPointIds,
                    this.showNumMap, this.showNumsArrays, this.numMap, this.numsArrays, this.canShowMap, this.canShowArrays);
        }

        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof RedPoint) {
                parseRedPointArrays(annotation);
            } else if (annotation instanceof ParentRedPoint) {
                parseParentRedPoint(annotation);
            } else if (annotation instanceof RelativeRedPoint) {
                parseRelativeRedPointArrays(annotation);
            }
        }

        private void parseRedPointArrays(Annotation annotation) {
            this.redPoints = ((RedPoint)annotation).value();
        }

        private void parseParentRedPoint(Annotation annotation) {
            this.parentRedPointId = ((ParentRedPoint) annotation).value();
        }

        private void parseRelativeRedPointArrays(Annotation annotation) {
            String[] relativeRedPoints = ((RelativeRedPoint) annotation).value();
            parseRelativeRedPointArrays(relativeRedPoints);
        }

        private void parseRelativeRedPointArrays(String[] relativeRedPoints) {
            for (String relativeRedPoint : relativeRedPoints) {
                int colon = relativeRedPoint.indexOf(':');
                if (colon == -1 || colon == 0 || colon == relativeRedPoint.length() - 1) {
                    throw methodError(
                            "@RelativeRedPoint value must be in the form \"Name: Value\". Found: \"%s\"", relativeRedPoint);
                }
                String relativeParentId = relativeRedPoint.substring(0, colon);
                String currentId = relativeRedPoint.substring(colon + 1).trim();
                if (this.relativeRedPointIds == null) {
                    this.relativeRedPointIds = new HashMap<>();
                }
                this.relativeRedPointIds.put(relativeParentId, currentId);
            }
        }

        private void parseParameterAnnotations(int p, Annotation[] annotations) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof RedPointNum) {
                    parseRedPointNum(p, annotation);
                } else if (annotation instanceof RedPointShowNum) {
                    parseRedPointShowNum(p, annotation);
                } else if (annotation instanceof RedPointCanShow) {
                    parseRedPointCanShow(p, annotation);
                } else if (annotation instanceof RedPointNumArrays) {
                    parseRedPointNumArrays(p, annotation);
                } else if (annotation instanceof RedPointShowNumArrays) {
                    parseRedPointShowNumsArrays(p, annotation);
                }  else if (annotation instanceof RedPointCanShowArrays) {
                    parseREdPointCanShowArrays(p, annotation);
                }
            }
        }

        private void parseRedPointNum(int p, Annotation annotation) {
            if (numMap == null) {
                numMap = new HashMap<>();
            }

            numMap.put(((RedPointNum)annotation).value(), (Integer) args[p]);
        }

        private void parseRedPointShowNum(int p, Annotation annotation) {
            if (showNumMap == null) {
                showNumMap = new HashMap<>();
            }

            showNumMap.put(((RedPointShowNum)annotation).value(), (Boolean)args[p]);
        }

        private void parseRedPointCanShow(int p, Annotation annotation) {
            if (canShowMap == null) {
                canShowMap = new HashMap<>();
            }

            canShowMap.put(((RedPointCanShow)annotation).value(), (Boolean) args[p]);
        }

        private void parseRedPointNumArrays(int p, Annotation annotation) {
            numsArrays = (int[]) args[p];
        }

        private void parseRedPointShowNumsArrays(int p, Annotation annotation) {
            showNumsArrays = (boolean[]) args[p];
        }

        private void parseREdPointCanShowArrays(int p, Annotation annotation) {
            canShowArrays = (boolean[]) args[p];
        }

        private RuntimeException methodError(String message, Object... args) {
            return methodError(null, message, args);
        }

        private RuntimeException methodError(Throwable cause, String message, Object... args) {
            message = String.format(message, args);
            return new IllegalArgumentException(message
                    + "\n    for method "
                    + method.getDeclaringClass().getSimpleName()
                    + "."
                    + method.getName(), cause);
        }

        private RuntimeException parameterError(int p, String message, Object... args) {
            return methodError(message + " (parameter #" + (p + 1) + ")", args);
        }
    }
}
