import { useEffect, useRef } from "react";

interface Marker {
  lat: number;
  lng: number;
  label?: string;
}

interface Props {
  center: google.maps.LatLngLiteral;
  markers?: Marker[];
  height?: number; // px
}

export default function Map({ center, markers = [], height = 300 }: Props) {
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!window.google || !ref.current) return;

    const map = new google.maps.Map(ref.current, {
      center,
      zoom: 5,
      disableDefaultUI: true,
      gestureHandling: "greedy",
    });

    markers.forEach(m =>
      new google.maps.Marker({
        position: { lat: m.lat, lng: m.lng },
        map,
        title: m.label,
      })
    );
  }, [center, markers]);

  return <div ref={ref} style={{ height }} className="rounded-lg shadow" />;
}