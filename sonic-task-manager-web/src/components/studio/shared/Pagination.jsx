import React from 'react';

/**
 * Pagination Component
 * Smart pagination with page size controls
 */
const Pagination = ({ 
  currentPage, 
  totalPages, 
  totalElements, 
  pageSize, 
  onPageChange, 
  onPageSizeChange 
}) => {
  const generatePageNumbers = () => {
    const delta = 2; // Pages to show on each side of current page
    const pages = [];
    const start = Math.max(0, currentPage - delta);
    const end = Math.min(totalPages - 1, currentPage + delta);

    // Add first page if not in range
    if (start > 0) {
      pages.push(0);
      if (start > 1) {
        pages.push('...');
      }
    }

    // Add pages in range
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    // Add last page if not in range
    if (end < totalPages - 1) {
      if (end < totalPages - 2) {
        pages.push('...');
      }
      pages.push(totalPages - 1);
    }

    return pages;
  };

  const pageNumbers = generatePageNumbers();
  const startItem = currentPage * pageSize + 1;
  const endItem = Math.min((currentPage + 1) * pageSize, totalElements);

  return (
    <div className="pagination">
      <div className="pagination-info">
        <span>
          Showing {startItem}-{endItem} of {totalElements} tasks
        </span>
        
        <div className="page-size-control">
          <label>
            Show:
            <select 
              value={pageSize} 
              onChange={(e) => onPageSizeChange(Number(e.target.value))}
            >
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>
            per page
          </label>
        </div>
      </div>

      <div className="pagination-controls">
        <button 
          className="pagination-btn"
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 0}
        >
          ← Previous
        </button>

        <div className="page-numbers">
          {pageNumbers.map((page, index) => (
            page === '...' ? (
              <span key={`ellipsis-${index}`} className="pagination-ellipsis">
                ...
              </span>
            ) : (
              <button
                key={page}
                className={`page-number ${page === currentPage ? 'page-number--active' : ''}`}
                onClick={() => onPageChange(page)}
              >
                {page + 1}
              </button>
            )
          ))}
        </div>

        <button 
          className="pagination-btn"
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage >= totalPages - 1}
        >
          Next →
        </button>
      </div>
    </div>
  );
};

export default Pagination;