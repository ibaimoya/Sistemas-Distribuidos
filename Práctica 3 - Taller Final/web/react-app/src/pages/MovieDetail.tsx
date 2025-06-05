import { useParams } from 'react-router-dom';

const MovieDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();

  return (
    <div className="min-h-screen flex items-center justify-center bg-black text-white">
      <h2 className="text-2xl">Detalle peli #{id}</h2>
    </div>
  );
};

export default MovieDetail;